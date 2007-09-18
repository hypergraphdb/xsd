/*
 * This file is part of the HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.util.HashMap;
import java.util.Map;

import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGHandleFactory;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.LazyRef;
import org.hypergraphdb.atom.HGAtomRef;
import org.hypergraphdb.type.AtomRefType;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.LinkRecord;
import org.hypergraphdb.type.Record;
import org.hypergraphdb.type.Slot;
import org.hypergraphdb.type.TypeUtils;

/**
 * This will be actually generated by CTC.
 */
public class USAddressComplexRecordType implements HGAtomType
{
   private HyperGraph hg;
 
   
   public Object make(HGPersistentHandle hgpersistenthandle, LazyRef lazyref, LazyRef lazyref1)
   {
       HashMap hashmap = new HashMap();
       HGPersistentHandle ahgpersistenthandle[] = hg.getStore().getLink(hgpersistenthandle);
       
       HGPersistentHandle hgpersistenthandle1;
       org.hypergraphdb.type.HGAtomType hgatomtype;
       Object obj;
       
       if(null!=ahgpersistenthandle[1])
       {
       hgpersistenthandle1 = HGHandleFactory.makeHandle("8b46b788-6587-11dc-84c8-001617d7885d");
       hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle1);
       obj = TypeUtils.makeValue(hg, ahgpersistenthandle[1], hgatomtype);
       hashmap.put("name", obj);
       }
       
       hgpersistenthandle1 = HGHandleFactory.makeHandle("8b46b788-6587-11dc-84c8-001617d7885d");
       hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle1);
       obj = TypeUtils.makeValue(hg, ahgpersistenthandle[3], hgatomtype);
       hashmap.put("street", obj);
       hgpersistenthandle1 = HGHandleFactory.makeHandle("8b46b788-6587-11dc-84c8-001617d7885d");
       hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle1);
       obj = TypeUtils.makeValue(hg, ahgpersistenthandle[5], hgatomtype);
       hashmap.put("city", obj);
       hgpersistenthandle1 = HGHandleFactory.makeHandle("8b46b788-6587-11dc-84c8-001617d7885d");
       hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle1);
       obj = TypeUtils.makeValue(hg, ahgpersistenthandle[7], hgatomtype);
       hashmap.put("state", obj);
       hgpersistenthandle1 = HGHandleFactory.makeHandle("8b529e10-6587-11dc-84c8-001617d7885d");
       hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle1);
       obj = TypeUtils.makeValue(hg, ahgpersistenthandle[9], hgatomtype);
       hashmap.put("zip", obj);
       hgpersistenthandle1 = HGHandleFactory.makeHandle("eeee128b-c5c1-11d9-bfe0-4b9280693a83");
       hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle1);
       obj = TypeUtils.makeValue(hg, ahgpersistenthandle[11], hgatomtype);
       hashmap.put("country", obj);
       return hashmap;
   }


   public void release(
      HGPersistentHandle handle)
   {
      // TODO Auto-generated method stub
   }

   /**
    * 
    */
   public void setHyperGraph(
      HyperGraph hg)
   {
      this.hg = hg;
   }

   public HGPersistentHandle store(Object obj)
   {
       Map map = (Map)obj;
       HGPersistentHandle hgpersistenthandle = HGHandleFactory.makeHandle("cea7c419-6581-11dc-920c-001617d7885d");
       org.hypergraphdb.type.HGAtomType hgatomtype = hg.getTypeSystem().getType(hgpersistenthandle);
       HGPersistentHandle ahgpersistenthandle[] = new HGPersistentHandle[12];
       hgpersistenthandle = HGHandleFactory.makeHandle("cea7c419-6581-11dc-920c-001617d7885d");
       ahgpersistenthandle[0] = hg.getPersistentHandle(hgpersistenthandle);
       
       Object obj1 = map.get("name");
       if(null!=obj1)
          ahgpersistenthandle[1] = TypeUtils.storeValue(hg, obj1, hg.getTypeSystem().getType(hgpersistenthandle));
       
       hgpersistenthandle = HGHandleFactory.makeHandle("cea7c419-6581-11dc-920c-001617d7885d");
       ahgpersistenthandle[2] = hg.getPersistentHandle(hgpersistenthandle);
       obj1 = map.get("street");
       ahgpersistenthandle[3] = TypeUtils.storeValue(hg, obj1, hg.getTypeSystem().getType(hgpersistenthandle));
       hgpersistenthandle = HGHandleFactory.makeHandle("cea7c419-6581-11dc-920c-001617d7885d");
       ahgpersistenthandle[4] = hg.getPersistentHandle(hgpersistenthandle);
       obj1 = map.get("city");
       ahgpersistenthandle[5] = TypeUtils.storeValue(hg, obj1, hg.getTypeSystem().getType(hgpersistenthandle));
       hgpersistenthandle = HGHandleFactory.makeHandle("cea7c419-6581-11dc-920c-001617d7885d");
       ahgpersistenthandle[6] = hg.getPersistentHandle(hgpersistenthandle);
       obj1 = map.get("state");
       ahgpersistenthandle[7] = TypeUtils.storeValue(hg, obj1, hg.getTypeSystem().getType(hgpersistenthandle));
       hgpersistenthandle = HGHandleFactory.makeHandle("cebf9281-6581-11dc-920c-001617d7885d");
       ahgpersistenthandle[8] = hg.getPersistentHandle(hgpersistenthandle);
       obj1 = map.get("zip");
       ahgpersistenthandle[9] = TypeUtils.storeValue(hg, obj1, hg.getTypeSystem().getType(hgpersistenthandle));
       hgpersistenthandle = HGHandleFactory.makeHandle("eeee128b-c5c1-11d9-bfe0-4b9280693a83");
       ahgpersistenthandle[10] = hg.getPersistentHandle(hgpersistenthandle);
       obj1 = map.get("country");
       ahgpersistenthandle[11] = TypeUtils.storeValue(hg, obj1, hg.getTypeSystem().getType(hgpersistenthandle));
       HGPersistentHandle hgpersistenthandle1 = TypeUtils.getNewHandleFor(hg, map);
       hg.getStore().store(hgpersistenthandle1, ahgpersistenthandle);
       return hgpersistenthandle1;
   }

   /**
    * 
    */
   public boolean subsumes(
      Object general, Object specific)
   {
      // TODO Auto-generated method stub
      return false;
   }

}
