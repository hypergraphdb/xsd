/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd.primitive;

import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.type.HGAtomType;

/**
 *
 */
public class XSDDoublePrimitive implements HGAtomType
{
    private HGAtomType hgdbType = null;

    public void setHyperGraph(HyperGraph hg)
    {
        hgdbType = hg.getTypeSystem().getAtomType(Double.class);
        hgdbType.setHyperGraph(hg);
    }

    public Object make(HGPersistentHandle handle,
                       LazyRef targetSet,
                       IncidenceSetRef incidenceSet)
    {
        return hgdbType.make(handle, targetSet, incidenceSet);
    }

    /**
     *
     * @param value String
     * @return boolean
     */
    public boolean evaluateRestrictions(double value)
    {
        return true;
    }

    public HGPersistentHandle store(Object o)
    {
        Double instance = (Double)o;
        /**@todo evaluate restrictions */
        boolean passes = evaluateRestrictions(instance);

        return hgdbType.store(instance);
    }

    public void release(HGPersistentHandle handle)
    {
        hgdbType.release(handle);
    }

    public boolean subsumes(Object general, Object specific)
    {
        return false;
    }

}
