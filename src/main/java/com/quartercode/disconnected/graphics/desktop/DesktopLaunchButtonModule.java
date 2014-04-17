/*
 * This file is part of Disconnected.
 * Copyright (c) 2013 QuarterCode <http://www.quartercode.com/>
 *
 * Disconnected is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disconnected is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disconnected. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.disconnected.graphics.desktop;

import com.quartercode.disconnected.graphics.AbstractGraphicsModule;
import com.quartercode.disconnected.graphics.GraphicsState;
import com.quartercode.disconnected.util.ResourceBundles;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;

/**
 * The desktop launch button module adds a launch {@link Button} with the theme <code>launch-button</code>.
 * The launch button can be used to start front-end-programs.
 * The actual button object is available under the key <code>button</code>.
 */
public class DesktopLaunchButtonModule extends AbstractGraphicsModule {

    private Button button;

    @Override
    public void add(final GraphicsState state) {

        button = new Button();
        button.setTheme("launch-button");
        button.setText(ResourceBundles.DESKTOP.getString("launch.button"));
        button.addCallback(new Runnable() {

            @Override
            public void run() {

                // TODO: Display launch menu
            }
        });
        ((Widget) state.getModule("desktopWidget").getValue("widget")).add(button);
        setValue("button", button);
    }

    @Override
    public void layout(GraphicsState state) {

        button.adjustSize();
        button.setPosition(10, state.getHeight() - button.getHeight() - 10);
    }

}
