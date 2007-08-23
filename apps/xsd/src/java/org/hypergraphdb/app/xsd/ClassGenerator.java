/*
 * This file is part of the XSD for HyperGraphDB source distribution. This is copyrighted
 * software. For permitted uses, licensing options and redistribution, please see
 * the LicensingInformation file at the root level of the distribution.
 *
 * Copyright (c) 2007
 * Kobrix Software, Inc.  All rights reserved.
 */
package org.hypergraphdb.app.xsd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.app.xsd.facet.ClusiveFacet;
import org.hypergraphdb.app.xsd.facet.ConstrainingFacet;
import org.hypergraphdb.app.xsd.facet.FractionDigitsFacet;
import org.hypergraphdb.app.xsd.facet.MaxExclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MaxInclusiveFacet;
import org.hypergraphdb.app.xsd.facet.MinInclusiveFacet;
import org.hypergraphdb.app.xsd.facet.TotalDigitsFacet;
import org.hypergraphdb.app.xsd.primitive.XSDDecimalPrimitive;
import org.hypergraphdb.type.HGAtomType;
import org.hypergraphdb.type.RecordType;
import org.hypergraphdb.type.Slot;
import org.hypergraphdb.type.TypeUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 
 */
public class ClassGenerator
{
   private static class CGPrivateClassLoader extends ClassLoader
   {
      public CGPrivateClassLoader()
      {
      }

      public Class defineClass(
         final String name, final byte[] b)
      {
         return defineClass(name, b, 0, b.length);
      }
   }

   /**
    * 
    * @param baseClassName
    *           String
    */
   public Class implementEvaluate(
      final Class clazz, Collection<ConstrainingFacet> facets)
   {
      Class result = clazz;

      if (XSDDecimalPrimitive.class == clazz)
      {
         String clazzName = clazz.getName();
         clazzName = clazzName.replace('.', '/');

         ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC, "GeneratedClass", null, clazzName,
               null);

         // creates a MethodWriter for the (implicit) constructor
         MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null,
               null);
         // pushes the 'this' variable
         mw.visitVarInsn(Opcodes.ALOAD, 0);
         // invokes the super class constructor
         mw.visitMethodInsn(Opcodes.INVOKESPECIAL, clazzName, "<init>", "()V");
         mw.visitInsn(Opcodes.RETURN);
         // this code uses a maximum of one stack element and one local variable
         mw.visitMaxs(1, 1);
         mw.visitEnd();

         // creates a MethodWriter for the 'main' method
         mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "evaluateRestrictions",
               "(Ljava/math/BigDecimal;)Z", null, null);
         Label l0 = new Label();

         // generate code for each facet.
         for (ConstrainingFacet facet : facets)
         {
            /** @todo some other more tabular way ? */
            if (facet instanceof MinInclusiveFacet)
            {
               ClusiveFacet clusive = (ClusiveFacet) facet;
               ClassGenerator.processClusive(mw, clusive, Opcodes.IF_ICMPGE);
            } else if (facet instanceof MaxInclusiveFacet)
            {
               ClusiveFacet clusive = (ClusiveFacet) facet;
               ClassGenerator.processClusive(mw, clusive, Opcodes.IF_ICMPLE);
            } else if (facet instanceof MaxExclusiveFacet)
            {
               ClusiveFacet clusive = (ClusiveFacet) facet;
               ClassGenerator.processClusive(mw, clusive, Opcodes.IF_ICMPLT);
            } else if (facet instanceof MinInclusiveFacet)
            {
               ClusiveFacet clusive = (ClusiveFacet) facet;
               ClassGenerator.processClusive(mw, clusive, Opcodes.IF_ICMPGT);
            } else if (facet instanceof TotalDigitsFacet)
            {
               TotalDigitsFacet totalDigits = (TotalDigitsFacet) facet;
               ClassGenerator.processTotalDigits(mw, totalDigits);
            } else if (facet instanceof FractionDigitsFacet)
            {
               FractionDigitsFacet fractionDigits = (FractionDigitsFacet) facet;
               processFractionDigits(mw, fractionDigits);
            }
         }

         mw.visitLabel(l0);
         mw.visitInsn(Opcodes.ICONST_1);
         mw.visitInsn(Opcodes.IRETURN);
         mw.visitMaxs(6, 6);
         mw.visitEnd();

         byte[] byteCode = cw.toByteArray();
         CGPrivateClassLoader cl = new CGPrivateClassLoader();
         result = cl.defineClass("GeneratedClass", byteCode);
      }

      return result;
   } // implementEvaluate.

   /**
    * 
    * @param mw
    *           MethodVisitor
    * @param l0
    *           Label
    * @param minInclusive
    *           MinInclusiveFacet
    */
   private static void processClusive(
      MethodVisitor mw, ClusiveFacet clusive, int opCode)
   {
      // make a big decimal instance.
      mw.visitVarInsn(Opcodes.ALOAD, 0);
      mw.visitTypeInsn(Opcodes.NEW, "java/math/BigDecimal");
      mw.visitInsn(Opcodes.DUP);
      mw.visitLdcInsn(clusive.getLimit());
      mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/math/BigDecimal", "<init>",
            "(Ljava/lang/String;)V");

      mw.visitVarInsn(Opcodes.ASTORE, 2);

      mw.visitInsn(Opcodes.ICONST_0);
      mw.visitVarInsn(Opcodes.ALOAD, 2);
      mw.visitVarInsn(Opcodes.ALOAD, 1);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/math/BigDecimal", "compareTo",
            "(Ljava/math/BigDecimal;)I");

      Label label = new Label();
      mw.visitJumpInsn(opCode, label);
      mw.visitInsn(Opcodes.ICONST_0);
      mw.visitInsn(Opcodes.IRETURN);
      mw.visitLabel(label);
   }

   /**
    * Generates code that evaluates the totalDigits restriction.
    */
   private static void processTotalDigits(
      MethodVisitor mw, TotalDigitsFacet totalDigits)
   {
      mw.visitVarInsn(Opcodes.ALOAD, 0);

      // get a string representation of the big decimal argument.
      mw.visitVarInsn(Opcodes.ALOAD, 1);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/math/BigDecimal", "toPlainString",
            "()Ljava/lang/String;");
      mw.visitVarInsn(Opcodes.ASTORE, 3);

      // store the length in a variable.
      mw.visitVarInsn(Opcodes.ALOAD, 3);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I");
      mw.visitVarInsn(Opcodes.ISTORE, 4);

      // look for the '.' character.
      mw.visitInsn(Opcodes.ICONST_M1);
      mw.visitVarInsn(Opcodes.ALOAD, 3);
      mw.visitIntInsn(Opcodes.BIPUSH, 46); // '.'
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "indexOf", "(I)I");

      // was there a '.' match ?
      Label l1 = new Label();
      mw.visitJumpInsn(Opcodes.IF_ICMPEQ, l1);
      mw.visitIincInsn(4, -1);
      mw.visitLabel(l1);

      // look for the '-' character.
      mw.visitInsn(Opcodes.ICONST_M1);
      mw.visitVarInsn(Opcodes.ALOAD, 3);
      mw.visitIntInsn(Opcodes.BIPUSH, 45); // '-'
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "indexOf", "(I)I");

      // was there a '.' match ?
      Label l2 = new Label();
      mw.visitJumpInsn(Opcodes.IF_ICMPEQ, l2);
      mw.visitIincInsn(4, -1);
      mw.visitLabel(l2);

      // compare the count of digits against the total digits argument.
      mw.visitVarInsn(Opcodes.ILOAD, 4);
      mw.visitLdcInsn(totalDigits.getValue());
      Label l3 = new Label();
      mw.visitJumpInsn(Opcodes.IF_ICMPLE, l3);
      mw.visitInsn(Opcodes.ICONST_0);
      mw.visitInsn(Opcodes.IRETURN);
      mw.visitLabel(l3);
   } // processTotalDigits.

   /**
    * Generates code that evaluates the totalDigits restriction.
    */
   private void processFractionDigits(
      MethodVisitor mw, FractionDigitsFacet totalDigits)
   {
      mw.visitVarInsn(Opcodes.ALOAD, 0);

      // get a string representation of the big decimal argument.
      mw.visitVarInsn(Opcodes.ALOAD, 1);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/math/BigDecimal", "toPlainString",
            "()Ljava/lang/String;");
      mw.visitVarInsn(Opcodes.ASTORE, 3);

      // get the last index of the '.' character.
      mw.visitVarInsn(Opcodes.ALOAD, 3);
      mw.visitIntInsn(Opcodes.BIPUSH, 46); // '.'
      mw.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL, "java/lang/String", "lastIndexOf", "(I)I");
      mw.visitVarInsn(Opcodes.ISTORE, 4);

      // compare against -1.
      Label l0 = new Label();
      mw.visitInsn(Opcodes.ICONST_M1);
      mw.visitVarInsn(Opcodes.ILOAD, 4);
      mw.visitJumpInsn(Opcodes.IF_ICMPEQ, l0);

      // if there is a '.' character determine the count of the fraction digits.
      mw.visitVarInsn(Opcodes.ALOAD, 3);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I");
      mw.visitVarInsn(Opcodes.ILOAD, 4);
      mw.visitInsn(Opcodes.ISUB);
      mw.visitInsn(Opcodes.ICONST_1);
      mw.visitInsn(Opcodes.ISUB);
      mw.visitVarInsn(Opcodes.ISTORE, 4);

      // compare the actual count against the specified limit.
      mw.visitLabel(l0);
      mw.visitVarInsn(Opcodes.ILOAD, 4);
      mw.visitVarInsn(Opcodes.ILOAD, totalDigits.getValue());
      Label l1 = new Label();
      mw.visitJumpInsn(Opcodes.IF_ICMPLE, l1);

      // return false if the restriction is not satisfied.
      mw.visitInsn(Opcodes.ICONST_0);
      mw.visitInsn(Opcodes.IRETURN);
      mw.visitLabel(l1);
   } // processFractionDigits.

   /**
    * 
    */
   public static Class generateComplexStore(
      final HyperGraph hg, final Class clazz, RecordType recordType)
   {
      Class result = clazz;

      HGHandle typeHandle = hg.getTypeSystem().getTypeHandle(
         "http://www.w3.org/2001/XMLSchema#string");
      HGAtomType type = hg.getTypeSystem().getType(
         "http://www.w3.org/2001/XMLSchema#string");
      HGPersistentHandle pTypeHandle = hg.getPersistentHandle(typeHandle);

      String clazzName = clazz.getName();
      clazzName = clazzName.replace('.', '/');

      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      cw.visit(Opcodes.V1_2, Opcodes.ACC_PUBLIC, "ComplexClass", null, clazzName, null);

      // creates a MethodWriter for the (implicit) constructor
      MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
      // pushes the 'this' variable
      mw.visitVarInsn(Opcodes.ALOAD, 0);
      // invokes the super class constructor
      mw.visitMethodInsn(Opcodes.INVOKESPECIAL, clazzName, "<init>", "()V");
      // this code uses a maximum of one stack element and one local variable
      mw.visitMaxs(1, 1);
      mw.visitEnd();

      // creates a MethodWriter for the 'main' method
      mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "store",
            "(Ljava/lang/Object;)V", null, null);
      Label l0 = new Label();
      
      //cast to Map.
      mw.visitVarInsn(Opcodes.ALOAD, 1);
      mw.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map" );
      mw.visitVarInsn(Opcodes.ASTORE, 2);

      //get the String atom type.
      mw.visitLdcInsn(pTypeHandle.toString());
      mw.visitMethodInsn(Opcodes.INVOKESTATIC, "org/hypergraphdb/HGHandleFactory",
         "makeHandle", "(Ljava/lang/String;)Lorg/hypergraphdb/HGPersistentHandle");
      mw.visitVarInsn(Opcodes.ASTORE, 3);
      
      mw.visitVarInsn(Opcodes.ALOAD, 0);
      mw.visitFieldInsn(Opcodes.GETFIELD, "ComplexClass", "hg", "Lorg/hypergraphdb/HyperGraph");
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/hypergraphdb/HyperGraph",
         "getTypeSystem", "()Lorg/hypergraphdb/HGTypeSystem");
      mw.visitVarInsn(Opcodes.ALOAD, 3);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/hypergraphdb/HGTypeSystem",
         "getType", "(Lorg/hypergraphdb/HGHandle;)Lorg/hypergraphdb/type/HGAtomType");
      mw.visitVarInsn(Opcodes.ASTORE, 4);
      
      //allocate a layout array.
      mw.visitIntInsn(Opcodes.BIPUSH, 2*recordType.getSlots().size());
      mw.visitTypeInsn(Opcodes.ANEWARRAY, "org/hypergraphdb/HGPersistentHandle");
      mw.visitVarInsn(Opcodes.ASTORE, 5);

      /**@todo couldn't start testing with the latest from svn.*/
      for(int i = 0; i < recordType.getSlots().size(); i++)
      {
         Slot slot = (Slot)hg.get(recordType.getSlots().get(j));
      
         //value type.
         mw.visitVarInsn(Opcodes.ALOAD, 5);
         mw.visitIntInsn(Opcodes.BIPUSH, i*2);
         mw.visitVarInsn(Opcodes.ALOAD, 0);
         mw.visitFieldInsn(Opcodes.GETFIELD, "ComplexClass", "hg", "Lorg/hypergraphdb/HyperGraph");
         mw.visitVarInsn(Opcodes.ALOAD, 3);
         mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/hypergraphdb/HyperGraph",
            "getPersistentHandle",
            "(Lorg/hypergraphdb/HGHandle;)Lorg/hypergraphdb/HGPersistentHandle");
         mw.visitInsn(Opcodes.AASTORE);
      
         mw.visitVarInsn(Opcodes.ALOAD, 2);
         mw.visitLdcInsn(slot.getLabel());
         mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "get",
            "(Ljava/lang/Object;)Ljava/lang/Object");
         mw.visitVarInsn(Opcodes.ASTORE, 6);

         //value.
         mw.visitVarInsn(Opcodes.ALOAD, 5);
         mw.visitIntInsn(Opcodes.BIPUSH, i*2 + 1);
         mw.visitVarInsn(Opcodes.ALOAD, 0);
         mw.visitFieldInsn(Opcodes.GETFIELD, "ComplexClass", "hg", "Lorg/hypergraphdb/HyperGraph");
         mw.visitVarInsn(Opcodes.ALOAD, 6);
         mw.visitVarInsn(Opcodes.ALOAD, 4);
         mw.visitMethodInsn(Opcodes.INVOKESTATIC, "org/hypergraphdb/type/TypeUtils", "storeValue",
            "(Lorg/hypergraphdb/HyperGraph;Ljava/lang/Object;Lorg/hypergraphdb/type/HGAtomType;)Lorg/hypergraphdb/HGPersistentHandle");
         mw.visitInsn(Opcodes.AASTORE);
      }

      //store the handles array.
      mw.visitVarInsn(Opcodes.ALOAD, 0);
      mw.visitFieldInsn(Opcodes.GETFIELD, "ComplexClass", "hg", "Lorg/hypergraphdb/HyperGraph");
      mw.visitVarInsn(Opcodes.ALOAD, 2);
      mw.visitMethodInsn(Opcodes.INVOKESTATIC, "org/hypergraphdb/type/TypeUtils",
         "getNewHandleFor",
         "(Lorg/hypergraphdb/HyperGraph;Ljava/lang/Object;)Lorg/hypergraphdb/HGPersistentHandle");
      mw.visitVarInsn(Opcodes.ASTORE, 7);
      
      mw.visitVarInsn(Opcodes.ALOAD, 0);
      mw.visitFieldInsn(Opcodes.GETFIELD, "ComplexClass", "hg", "Lorg/hypergraphdb/HyperGraph");
      mw.visitVarInsn(Opcodes.ALOAD, 7);
      mw.visitVarInsn(Opcodes.ALOAD, 5);
      mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/hypergraphdb/HGStore", "store",
         "(Lorg/hypergraphdb/HGPersistentHandle;[Lorg/hypergraphdb/HGPersistentHandle;)Lorg/hypergraphdb/HGPersistentHandle");
      mw.visitInsn(Opcodes.POP);

      //return.
      mw.visitVarInsn(Opcodes.ALOAD, 7);
      mw.visitInsn(Opcodes.ARETURN);
      
      mw.visitMaxs(5, 8);
      mw.visitEnd();

      byte[] byteCode = cw.toByteArray();
      
      File f = new File("bytecode.class");
      try
      {
         FileOutputStream o = new FileOutputStream(f);
         o.write(byteCode);
         o.close();
      } catch (Exception e)
      {
         e.printStackTrace();
      }
      
      CGPrivateClassLoader cl = new CGPrivateClassLoader();
      result = cl.defineClass("GeneratedClass", byteCode);

      return result;
   }
   
} // ClassGenerator.
