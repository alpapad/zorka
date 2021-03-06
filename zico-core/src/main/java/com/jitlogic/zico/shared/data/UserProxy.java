/**
 * Copyright 2012-2014 Rafal Lewczuk <rafal.lewczuk@jitlogic.com>
 * <p/>
 * This is free software. You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jitlogic.zico.shared.data;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.jitlogic.zico.core.UserManager;
import com.jitlogic.zico.core.model.User;

import java.util.List;

@ProxyFor(value = User.class, locator = UserManager.class)
public interface UserProxy extends EntityProxy {

    public String getUserName();

    public void setUserName(String userName);

    public String getRealName();

    public void setRealName(String realName);

    public boolean isAdmin();

    public void setAdmin(boolean admin);

    public List<String> getAllowedHosts();

    public void setAllowedHosts(List<String> hosts);
}
