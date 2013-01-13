///*
// * Copyright (C) 2012 daniel
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package darwin.util.logging;
//
//import org.apache.log4j.*;
//import org.apache.log4j.spi.LoggingEvent;
//
///**
// *
// * @author daniel
// */
//public class JavaScripAppender extends AppenderSkeleton {
//
//    private Layout l = new SimpleLayout();
//
//    @Override
//    protected void append(LoggingEvent event) {
//        String msg = l.format(event).trim();
//        String style = "default";
//        switch (event.getLevel().toInt()) {
//            case Level.WARN_INT:
//                style = "warning";
//                break;
//            case Level.ERROR_INT:
//                style = "error";
//                break;
//            case Level.FATAL_INT:
//                style = "fatal";
//        }
//        throw new UnsupportedOperationException("JavaScript nicht implementiert");
////        try {
////            JSObject window = JSObject.getWindow(applet);
////            window.eval("print('" + style + "', '" + msg + "');");
////        } catch (JSException ex) {
////            new Exception("Der Aufruf der 'print' javascript Function,"
////                    + " hat eine Exception geworfen!", ex).printStackTrace();
////        }
//    }
//
//    @Override
//    public void close() {
//    }
//
//    @Override
//    public boolean requiresLayout() {
//        return false;
//    }
//}
