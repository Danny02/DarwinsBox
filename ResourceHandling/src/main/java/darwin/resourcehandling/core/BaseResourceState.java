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
//package darwin.resourcehandling.core;
//
//import darwin.resourcehandling.*;
//
///**
// *
// * @author daniel
// */
//public class BaseResourceState<T extends ResourceHandle> implements ResourceState<T>, ResourceBuilder<T> {
//    private final T handle;
//
//    public BaseResourceState(T handle) {
//        this.handle = handle;
//    }
//
//    @Override
//    public ResourceBuilder<T> getBuilder() {
//        return this;
//    }
//
//    @Override
//    public ResourceState neededBaseState() {
//        return null;
//    }
//
//    @Override
//    public T createResource(Resource base) throws WrongBuildArgsException {
//        return handle;
//    }
//
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        return true;
//    }
//
//    public int hashCode() {
//        int hash = 5;
//        return hash;
//    }
//}
