package com.jitlogic.zorka.agent.zabbix;

import com.jitlogic.zorka.agent.JmxObject;
import com.jitlogic.zorka.agent.ZorkaBshAgent;
import com.jitlogic.zorka.agent.ZorkaLib;
import com.jitlogic.zorka.util.ObjectInspector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * This library module
 *
 * @author RLE <rle@jitlogic.com>
 *
 */
public class ZabbixLib {

    private ZorkaBshAgent bshAgent;
    private ZorkaLib zorkaLib;

    private ObjectInspector inspector = new ObjectInspector();

    public ZabbixLib(ZorkaBshAgent bshAgent, ZorkaLib zorkaLib) {
        this.bshAgent = bshAgent;
        this.zorkaLib = zorkaLib;
    }

    /**
     *
     * @param mbs
     * @param filter
     * @param attrs
     *
     * @return
     */
    public JSONObject discovery(String mbs, String filter, String...attrs) {
        return discovery(mbs,filter, attrs, new String[0], new String[0]);
    }


    /**
     *
     * @param mbs
     * @param filter
     * @param oattrs
     * @param path
     * @param pattrs
     * @return
     */
    public JSONObject discovery(String mbs, String filter, String[] oattrs, String[] path, String[] pattrs) {

        List<Object> osrc = zorkaLib.jmxList(Arrays.asList((Object)mbs, filter));
        JSONArray    dsrc = new JSONArray();

        // List objects from mbean server
        for (Object obj : osrc) {
            if (obj instanceof JmxObject) {
                ObjectName on = ((JmxObject)obj).getName();
                JSONObject odo = new JSONObject();
                for (String attr : oattrs) {
                    String atval = on.getKeyProperty(attr);
                    if (atval != null) {
                        odo.put("{#" + attr.toUpperCase() + "}", atval);
                    }
                }
                if (odo.size() > 0) {
                    dsrc.add(odo);
                } else {
                    dsrc.add(null);
                }
            } else {
                dsrc.add(null);
            }
        }

        // Iterate over fetched list, resolve path and add attributes
        for (int pidx = 0; pidx < path.length; pidx++) {
            List<Object> odst = new ArrayList<Object>(osrc.size());
            JSONArray ddst = new JSONArray();

            String pathItem = path[pidx], pathAttr = pattrs[pidx];

            for (int oidx = 0; oidx < osrc.size(); oidx++) {

                Object srcObj = osrc.get(oidx);
                JSONObject dstObj = (JSONObject)dsrc.get(oidx);

                if (dstObj == null) continue;

                if (pathItem.startsWith("~")) {
                    for (String attr : inspector.list(srcObj)) {
                        if (attr != null && attr.matches(pathItem.substring(1))) {
                            Object obj = inspector.get(srcObj, attr);
                            if (obj != null) {
                                JSONObject dsr = pathAttr == null ? dstObj
                                        : extend(dstObj, pathAttr, attr);
                                odst.add(obj); ddst.add(dsr);
                            }
                        }
                    }
                } else {
                    Object obj = inspector.get(srcObj, pathItem);
                    if (obj != null) {
                        JSONObject dsr = pathAttr == null ? dstObj
                            : extend(dstObj, pathAttr, pathItem);
                        odst.add(obj); ddst.add(dsr);
                    }
                } //
            } // for (int oidx = 0 ...
            osrc = odst; dsrc = ddst;
        } // for (int pidx = 0 ...

        JSONObject discoveries = new JSONObject();
        discoveries.put("data", dsrc);
        return discoveries;
    } // discovery()


    private JSONObject extend(JSONObject src, String key, String val) {
        JSONObject obj = new JSONObject();
        obj.putAll(src);

        obj.put("{#" + key.toUpperCase() + "}", val);

        return obj;
    }
}
