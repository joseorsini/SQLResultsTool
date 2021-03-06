package com.dotmarketing.osgi.viewtools;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

public class SQLResultsViewToolInfo extends ServletToolInfo {

    @Override
    public String getKey () {
        return "sqlResultsTool";
    }

    @Override
    public String getScope () {
        return ViewContext.REQUEST;
    }

    @Override
    public String getClassname () {
        return SQLResultsViewTool.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        SQLResultsViewTool viewTool = new SQLResultsViewTool();
        viewTool.init( initData );

        setScope( ViewContext.REQUEST );

        return viewTool;
    }

}