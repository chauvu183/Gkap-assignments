/*
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */

/**
 * @author Antoine Dutot <antoine.dutot@graphstream-project.org>
 * @author Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 * @author Hicham Brahimi <hicham.brahimi@graphstream-project.org>
 */

package de.haw_hamburg.gkap.ui;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.*;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.camera.DefaultCamera2D;
import org.graphstream.ui.view.util.InteractiveElement;
import org.graphstream.ui.view.util.MouseManager;

import java.util.EnumSet;

public class CustomMouseManager implements MouseManager {
    /**
     * The view this manager operates upon.
     */
    protected View view;

    /**
     * The graph to modify according to the view actions.
     */
    protected GraphicGraph graph;

    final private EnumSet<InteractiveElement> types;

    double dragBeginX, dragBeginY;
    private boolean dragCanvasActive = false;

    protected GraphicElement curElement;

    protected double x1, y1;


    public CustomMouseManager() {
        this(EnumSet.of(InteractiveElement.NODE, InteractiveElement.SPRITE, InteractiveElement.EDGE));

    }

    public CustomMouseManager(EnumSet<InteractiveElement> types) {
        this.types = types;
    }

    public void init(GraphicGraph graph, View view) {
        this.view = view;
        this.graph = graph;
        view.addListener(MouseEvent.MOUSE_PRESSED, mousePressed);
        view.addListener(MouseEvent.MOUSE_DRAGGED, mouseDragged);
        view.addListener(MouseEvent.MOUSE_RELEASED, mouseRelease);
        view.addListener(ScrollEvent.SCROLL, mouseScroll);
        view.addListener(KeyEvent.ANY, ctrlButton);
    }

    @Override
    public void release() {
        view.removeListener(MouseEvent.MOUSE_PRESSED, mousePressed);
        view.removeListener(MouseEvent.MOUSE_DRAGGED, mouseDragged);
        view.removeListener(MouseEvent.MOUSE_RELEASED, mouseRelease);
        view.removeListener(ScrollEvent.SCROLL, mouseScroll);
        view.removeListener(KeyEvent.ANY, ctrlButton);
    }

    EventHandler<MouseEvent> mousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.isMiddleButtonDown() || event.isControlDown()) {
                if (view instanceof FxViewPanel) {
                    ((FxViewPanel) view).setCursor(Cursor.MOVE);
                }

                dragCanvasActive = true;
                dragBeginX = event.getX();
                dragBeginY = event.getY();
                return;
            }

            //select single element
            curElement = view.findGraphicElementAt(types, event.getX(), event.getY());
            if (curElement != null) {
                view.freezeElement(curElement, true);
                if (event.getButton() == MouseButton.SECONDARY) {
                    curElement.setAttribute("ui.selected");
                } else {
                    curElement.setAttribute("ui.clicked");
                }
            } else {
                x1 = event.getX();
                y1 = event.getY();
                view.requireFocus();

                //begin selection when ctrl and shift and middle mouse are not pressed
                if (!event.isShiftDown() && !event.isMiddleButtonDown() && !event.isControlDown()) {
                    graph.nodes().filter(n -> n.hasAttribute("ui.selected")).forEach(n -> n.removeAttribute("ui.selected"));
                    graph.sprites().filter(s -> s.hasAttribute("ui.selected")).forEach(s -> s.removeAttribute("ui.selected"));
                    graph.edges().filter(el -> el.hasAttribute("ui.selected")).forEach(el -> el.removeAttribute("ui.selected"));
                    view.beginSelectionAt(x1, y1);
                }

            }
        }
    };

    EventHandler<MouseEvent> mouseDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!event.isControlDown() && !event.isMiddleButtonDown()) {
                if (view instanceof FxViewPanel) {
                    ((FxViewPanel) view).setCursor(Cursor.DEFAULT);
                }
                dragCanvasActive = false;
            }
            if (dragCanvasActive) {
                Camera camera = view.getCamera();
                double deltaX = (event.getX() - dragBeginX) * 0.002f * camera.getViewPercent() * camera.getGraphDimension();
                double deltaY = (event.getY() - dragBeginY) * 0.002f * camera.getViewPercent() * camera.getGraphDimension();
                dragBeginX = event.getX();
                dragBeginY = event.getY();
                Point3 currentCent = camera.getViewCenter();
                camera.setViewCenter(currentCent.x - deltaX, currentCent.y + deltaY, 0);
            } else if (curElement != null) {
                view.moveElementAtPx(curElement, event.getX(), event.getY());
            } else {
                view.selectionGrowsAt(event.getX(), event.getY());
            }
        }
    };

    EventHandler<MouseEvent> mouseRelease = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!event.isControlDown() && view instanceof FxViewPanel)
                ((FxViewPanel) view).setCursor(Cursor.DEFAULT);
            if (curElement != null) {
                view.freezeElement(curElement, false);
                if (event.getButton() != MouseButton.SECONDARY) {
                    curElement.removeAttribute("ui.clicked");
                }
                curElement = null;
            } else {
                double x2 = event.getX();
                double y2 = event.getY();
                double t;

                if (x1 > x2) {
                    t = x1;
                    x1 = x2;
                    x2 = t;
                }
                if (y1 > y2) {
                    t = y1;
                    y1 = y2;
                    y2 = t;
                }

                for (GraphicElement element : view.allGraphicElementsIn(types, x1, y1, x2, y2)) {
                    if (!element.hasAttribute("ui.selected"))
                        element.setAttribute("ui.selected");
                }
                view.endSelectionAt(x2, y2);
            }
        }
    };

    EventHandler<ScrollEvent> mouseScroll = new EventHandler<ScrollEvent>() {
        @Override
        public void handle(ScrollEvent event) {
            if (event.isControlDown() && event.getDeltaY() != 0 && view.getCamera() instanceof DefaultCamera2D) {
                Camera camera = view.getCamera();

                double x = event.getX();
                double y = event.getY();
                Point3 oldPosition = camera.transformPxToGu(x, y);
                if (event.getDeltaY() > 0) {
                    camera.setViewPercent(camera.getViewPercent() * 0.9f);
                } else if (event.getDeltaY() < 0) {
                    camera.setViewPercent(camera.getViewPercent() * 1.1d);
                }
                //calculate new cursor position on canvas
                ((DefaultCamera2D) camera).pushView(graph);
                Point3 newPosition = camera.transformPxToGu(x, y);
                Point3 cP = camera.getViewCenter();
                camera.setViewCenter(cP.x + (oldPosition.x - newPosition.x), cP.y + (oldPosition.y - newPosition.y), cP.z);
                ((DefaultCamera2D) camera).popView();
            }
        }
    };

    EventHandler<KeyEvent> ctrlButton = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.CONTROL && view instanceof FxViewPanel) {
                if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                    ((FxViewPanel) view).setCursor(Cursor.MOVE);
                } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                    ((FxViewPanel) view).setCursor(Cursor.DEFAULT);
                }
            }
        }
    };


    @Override
    public EnumSet<InteractiveElement> getManagedTypes() {
        return types;
    }
}
