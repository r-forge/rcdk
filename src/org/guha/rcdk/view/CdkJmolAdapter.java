/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.guha.rcdk.view;

import org.jmol.api.JmolAdapter;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Provides an interface to CDK IO and CDK data classes. The
 * <code>openBufferedReader</code> uses the ReaderFactory to get an CDK Reader.
 * The <code>getAtomIterator</code> and other methods that act on a
 * <code>clientFile</code> accept <code>AtomContainer</code>s and
 * <code>ChemFile</code>s.
 */
public class CdkJmolAdapter extends JmolAdapter {

    public final static String ATOM_SET_INDEX = "org.jmol.adapter.cdk.ATOM_SET_INDEX";


    public CdkJmolAdapter(Logger logger) {
        super("CdkJmolAdapter", logger);
    }

    /* **************************************************************
      * the file related methods
      * **************************************************************/

    public Object openBufferedReader(String name, BufferedReader bufferedReader) {
        IChemFile chemFile = null;
        try {
            ISimpleChemObjectReader chemObjectReader = null;
            try {
                chemObjectReader = new ReaderFactory()
                        .createReader(bufferedReader);
            } catch (IOException ex) {
                return "Jmol: Error determining input format: " + ex;
            }
            if (chemObjectReader == null) {
                return "Jmol: unrecognized input format";
            }
            chemFile = (IChemFile) chemObjectReader.read(new org.openscience.cdk.ChemFile());
        } catch (CDKException ex) {
            return "Error reading input:" + ex;
        }
        if (chemFile == null)
            return "unknown error reading file";
        AtomTypeFactory factory = AtomTypeFactory.getInstance(
                "pdb_atomtypes.txt", chemFile.getBuilder());
        IAtomContainer atomContainer = (IAtomContainer) ChemFileManipulator.getAllAtomContainers(chemFile);

        for (IAtom atom : atomContainer.atoms()) {
            try {
                if (atom instanceof IPDBAtom) {
                    // the PDBReader has the annoying feature to add the residue code
                    IPDBAtom pdbAtom = (IPDBAtom) atom;
                    String atName = pdbAtom.getAtomTypeName();
                    if (atName.indexOf(".") != -1) {
                        atName = atName.substring(atName.indexOf(".") + 1);
                    }
                    IAtomType type = factory.getAtomType(atName);
                    AtomTypeManipulator.configure(pdbAtom, type);
                } else {
                    factory.configure(atom);
                }
            } catch (CDKException exception) {

            }
        }
        return chemFile;
    }

    public String getFileTypeName(Object clientFile) {
        if (clientFile instanceof PDBPolymer) {
            return "pdb";
        }
        return "other";
    }

    public String getAtomSetCollectionName(Object clientFile) {

        if (clientFile instanceof IChemObject) {
            Object title = ((IChemObject) clientFile)
                    .getProperty(CDKConstants.TITLE);
            if (title != null) {

                return title.toString();
            }
        }
        return null;
    }

//    public Hashtable getAtomSetCollectionAuxiliaryInfo(Object clientFile) {
//        return null;
//    }

    /* **************************************************************
      * The frame related methods
      * **************************************************************/

    public int getAtomSetCount(Object clientFile) {

        if (clientFile instanceof IAtomContainer) {

            return 1;
        } else if (clientFile instanceof IChemFile) {

            return ChemFileManipulator.getAllChemModels((IChemFile) clientFile).size();
        } else {

            return 0;
        }
    }

    public int getEstimatedAtomCount(Object clientFile) {

        if (clientFile instanceof IAtomContainer) return ((IAtomContainer) clientFile).getAtomCount();
        if (clientFile instanceof IChemFile) return ChemFileManipulator.getAtomCount((IChemFile) clientFile);

        return 0;
    }

    /*
      this needs to be handled through the StructureIterator

      String[] getPdbStructureRecords(Object clientFile) {
      ChemFile chemFile = (ChemFile)clientFile;
      ChemSequence chemSequence = chemFile.getChemSequence(0);
      ChemModel chemModel = chemSequence.getChemModel(0);
      Vector structureVector =
      (Vector)chemModel.getProperty("pdb.structure.records");
      if (structureVector == null)
      return null;
      String[] t = new String[structureVector.size()];
      structureVector.copyInto(t);
      return t;
      }
      */

    public float[] getNotionalUnitcell(Object clientFile) {
        if (clientFile instanceof ICrystal) {
            ICrystal crystal = (ICrystal) clientFile;
            double[] notional = CrystalGeometryTools.cartesianToNotional(
                    crystal.getA(), crystal.getB(), crystal.getC());
            float[] fNotional = new float[6];
            for (int i = 0; i < 6; i++) {
                fNotional[i] = (float) notional[i];
            }
            return fNotional;
        } // else: no crystal thus no unit cell info
        return null;
    }

    public String getClientAtomStringProperty(Object clientAtom,
                                              String propertyName) {
        Object value = ((IAtom) clientAtom).getProperty(propertyName);
        return value == null ? null : "" + value;
    }

    public JmolAdapter.AtomIterator getAtomIterator(Object clientFile) {
        return new AtomIterator((IChemFile) clientFile);
    }

    public JmolAdapter.BondIterator getBondIterator(Object clientFile) {

        return new BondIterator((IChemFile) clientFile);
    }

    /* ***************************************************************
      * the frame iterators
      * **************************************************************/
    class AtomIterator extends JmolAdapter.AtomIterator {

        IChemFile chemFile;
        int atomCount, iatom;
        int modelCount, imodel;
        int icontainer;
        List models;
        List containers;

        IAtom atom;

        AtomIterator(IChemFile chemFile) {
            this.chemFile = chemFile;
            models = ChemFileManipulator.getAllChemModels(chemFile);
            modelCount = models.size();
            if (modelCount > 0) {
                containers = ChemModelManipulator.getAllAtomContainers((IChemModel) models.iterator().next());
                if (containers.iterator().hasNext())
                    atomCount = ((IAtomContainer) containers.get(0)).getAtomCount();
            }
            iatom = 0;
            imodel = 0;
            icontainer = 0;
        }

        public boolean hasNext() {
            if (modelCount == 0) return false;
            if (iatom == atomCount) {
                icontainer++;
                if (icontainer == containers.size()) {
                    imodel++;
                    if (imodel == modelCount) return false;
                    containers = ChemModelManipulator.getAllAtomContainers((IChemModel) models.get(imodel));
                    if (containers.size() == 0) return false;
                    icontainer = 0;
                }
                //FIXME: catch ArrayIndexOutOfBoundsException for next line
                try {
                    atomCount = ((IAtomContainer) containers.get(icontainer)).getAtomCount();
                } catch (ArrayIndexOutOfBoundsException e) {
                    return false;
                }
                iatom = 0;
                return hasNext();
            }
            atom = ((IAtomContainer) containers.get(icontainer)).getAtom(iatom++);
//			      bcLogger.debug("unique ID: " + getUniqueID());
//			      bcLogger.debug("element symbol: " + getElementSymbol());
//			      bcLogger.debug("element number: " + getElementNumber());
//			      bcLogger.debug("atom name: " + getAtomName());
//			      bcLogger.debug("formal charge: " + getFormalCharge());
//			      bcLogger.debug("partial charge: " + getPartialCharge());
//			      bcLogger.debug("x: " + getX());
//			      bcLogger.debug("y: " + getY());
//			      bcLogger.debug("z: " + getZ());
//			      bcLogger.debug("vx: " + getVectorX());
//			      bcLogger.debug("vy: " + getVectorY());
//			      bcLogger.debug("vz: " + getVectorZ());
//			      bcLogger.debug("b factor: " + getBfactor());
//			      bcLogger.debug("occupacy: " + getOccupancy());
//			      bcLogger.debug("is hetero: " + getIsHetero());
//			      bcLogger.debug("atom serial: " + getAtomSerial());
//			      bcLogger.debug("chain id: " + getChainID());
//			      bcLogger.debug("alternate location id: " + getAlternateLocationID());
//			      bcLogger.debug("group3: " + getGroup3());
//			      bcLogger.debug("sequence number: " + getSequenceNumber());
//			      bcLogger.debug("insertion code: " + getInsertionCode());
            return true;
        }

        public int getAtomSerial() {
            return iatom;
        }

        public Object getUniqueID() {
            return atom;
        }

        public int getElementNumber() {
            int atomicNum = atom.getAtomicNumber();
            if (atomicNum == 0)
                atomicNum = -1;
            return atomicNum;
        }

        public String getElementSymbol() {
            return atom.getSymbol();
        }

        public float getX() {
            return atom.getPoint3d() == null ? 0.0f : (float) atom.getPoint3d().x;
        }

        public float getY() {
            return atom.getPoint3d() == null ? 0.0f : (float) atom.getPoint3d().y;
        }

        public float getZ() {
            return atom.getPoint3d() == null ? 0.0f : (float) atom.getPoint3d().z;
        }

        public String getPdbAtomRecord() {
            return (String) atom.getProperty("pdb.record");
        }

        public String getAtomName() {
            if (atom instanceof IPDBAtom) {
                IPDBAtom pdbAtom = (IPDBAtom) atom;
                return pdbAtom.getName();
            }
            return super.getAtomName();
        }

        public int getAtomSetIndex() {
            return imodel;
        }

        public char getChainID() {
            if (atom instanceof IPDBAtom) {
                IPDBAtom pdbAtom = (IPDBAtom) atom;
                if (pdbAtom.getChainID().length() > 0)
                    return pdbAtom.getChainID().charAt(0);
            }
            return super.getChainID();
        }

        public String getGroup3() {
            if (atom instanceof IPDBAtom) {
                IPDBAtom pdbAtom = (IPDBAtom) atom;
                if (pdbAtom.getResName() != null && pdbAtom.getResName().length() > 0)
                    return pdbAtom.getResName();
            }
            return super.getGroup3();
        }

        public int getSequenceNumber() {
            if (atom instanceof IPDBAtom) {
                IPDBAtom pdbAtom = (IPDBAtom) atom;
                if (pdbAtom.getResSeq().length() > 0)
                    return Integer.parseInt(pdbAtom.getResSeq());
            }
            return super.getSequenceNumber();
        }

        public char getInsertionCode() {
            if (atom instanceof IPDBAtom) {
                IPDBAtom pdbAtom = (IPDBAtom) atom;
                if (pdbAtom.getICode().length() > 0)
                    return pdbAtom.getICode().charAt(0);
            }
            return super.getInsertionCode();
        }

        public Object getClientAtomReference() {
            return atom;
        }
    }

    class BondIterator extends JmolAdapter.BondIterator {

        IChemFile chemFile;
        int modelCount, imodel;
        int bondCount, ibond;
        int containerCount, icontainer;
        IBond bond;
        List models;
        List containers;

        BondIterator(IChemFile chemFile) {
            this.chemFile = chemFile;
            bondCount = 0;
            models = ChemFileManipulator.getAllChemModels(chemFile);
            modelCount = models.size();
            if (modelCount > 0) {
                containers = ChemModelManipulator.getAllAtomContainers((IChemModel) models.get(0));
                if (containerCount > 0)
                    bondCount = ((IAtomContainer) containers.get(0)).getBondCount();
            }
            ibond = 0;
            imodel = 0;
            icontainer = 0;
        }

        public boolean hasNext() {
            if (ibond == bondCount) {
                icontainer++;
                if (icontainer >= containerCount) {
                    imodel++;
                    if (imodel >= models.size()) return false;
                    containers = ChemModelManipulator.getAllAtomContainers((IChemModel) models.get(imodel));
                    containerCount = containers.size();
                    if (containerCount == 0) return false;
                    icontainer = 0;
                }
                bondCount = ((IAtomContainer) containers.get(icontainer)).getBondCount();
                ibond = 0;
                return hasNext();
            }
            // FIXME: skip all bonds which are messy, but this should really be examined more closely
            bond = ((IAtomContainer) containers.get(icontainer)).getBond(ibond++);
            boolean isOK = bond.getAtomCount() == 2 && (bond.getAtom(0) != null && bond.getAtom(1) != null);
            if (!isOK) {

                return hasNext();
            }
            // end work around
            return true;
        }

        public Object getAtomUniqueID1() {
            return (bond.getAtomCount() == 2) ? bond.getAtom(0) : null;
        }

        public Object getAtomUniqueID2() {
            return (bond.getAtomCount() == 2) ? bond.getAtom(1) : null;
        }

        public int getEncodedOrder() {
            IBond.Order order = bond.getOrder();
            if (order == IBond.Order.SINGLE) return 1;
            else if (order == IBond.Order.DOUBLE) return 2;
            else if (order == IBond.Order.TRIPLE) return 3;
            else return 4;
        }
    }

    public JmolAdapter.StructureIterator getStructureIterator(Object clientFile) {

        if (clientFile instanceof IChemFile)
            return new StructureIterator((IChemFile) clientFile);
        return null;
    }

    public class StructureIterator extends JmolAdapter.StructureIterator {
        int structureCount;
        Iterator structures;
        PDBStructure structure;

        int istructure;

        StructureIterator(IChemFile chemFile) {
            // OK, the structures are only defined in the first PDBPolymer
            this.structures = new ArrayList(0).iterator();
            if (chemFile == null) return;
            List models = ChemFileManipulator.getAllChemModels(chemFile);
            if (models.size() == 0) return;
            List containers = ChemModelManipulator.getAllAtomContainers((IChemModel) models.get(0));
            if (containers.size() == 0) return;
            if (!(containers.get(0) instanceof PDBPolymer)) return;
            PDBPolymer polymer = (PDBPolymer) containers.get(0);
            Collection pdbStructures = polymer.getStructures();
            if (pdbStructures == null) return;
            this.structures = pdbStructures.iterator();
        }

        @Override
        public boolean hasNext() {
            if (!structures.hasNext())
                return false;
            structure = (PDBStructure) structures.next();
            return true;
        }

        @Override
        public String getStructureType() {
            return structure.getStructureType();
        }

        @Override
        public char getStartChainID() {
            return structure.getStartChainID();
        }

        @Override
        public int getStartSequenceNumber() {
            return structure.getStartSequenceNumber();
        }

        @Override
        public char getStartInsertionCode() {
            return structure.getStartInsertionCode();
        }

        @Override
        public char getEndChainID() {
            return structure.getEndChainID();
        }

        @Override
        public int getEndSequenceNumber() {
            return structure.getEndSequenceNumber();
        }

        @Override
        public char getEndInsertionCode() {
            return structure.getEndInsertionCode();
        }

        @Override
        public int getModelIndex() {
            // FIXME: do something good here!!!
            return 1;
        }
    }
}
