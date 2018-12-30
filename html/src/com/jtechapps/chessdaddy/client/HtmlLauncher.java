package com.jtechapps.chessdaddy.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.jtechapps.chessdaddy.ChessDaddy;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(500, 500);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new ChessDaddy();
        }
}