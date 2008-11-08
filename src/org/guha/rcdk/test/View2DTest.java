package org.guha.rcdk.test;

import junit.framework.TestCase;
import org.guha.rcdk.util.Misc;
import org.guha.rcdk.view.ViewMolecule2D;
import org.guha.rcdk.view.ViewMolecule2DTable;
import org.guha.rcdk.view.ViewMolecule2Dv2;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Created by IntelliJ IDEA.
 * User: rguha
 * Date: Aug 28, 2006
 * Time: 2:58:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class View2DTest extends TestCase {
    String home = "/home/rguha/";

    public void testView2DFromSmiles() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer container = sp.parseSmiles("C1CN2CCN(CCCN(CCN(C1)Cc1ccccn1)CC2)C");
        ViewMolecule2D v2d = new ViewMolecule2D(container);
        v2d.draw();
        fail();
    }

    public void testView2D() throws CDKException {
        String[] fname = {home + "src/R/trunk/rcdk/data/dan001.sdf",
                home + "src/R/trunk/rcdk/data/dan002.sdf",
                home + "src/R/trunk/rcdk/data/dan003.sdf"};
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(fname, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        ViewMolecule2D v2d = new ViewMolecule2D(acs[1]);
        v2d.draw();
        fail();
    }

    public void testView2Dv2() throws Exception {
        String[] fname = {home + "src/R/trunk/rcdk/data/dan001.hin",
                home + "src/R/trunk/rcdk/data/dan002.hin",
                home + "src/R/trunk/rcdk/data/dan003.hin"};
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(fname, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        ViewMolecule2Dv2 v2d = new ViewMolecule2Dv2(acs[1]);
        fail();
    }

    public void testView2DT() {
        String[] fname = {home + "src/R/trunk/rcdk/data/dan001.hin",
                home + "src/R/trunk/rcdk/data/dan002.hin",
                home + "src/R/trunk/rcdk/data/dan008.hin"};
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(fname, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        ViewMolecule2DTable v2dt = new ViewMolecule2DTable(acs, 3, 200, 200);
        fail();
    }
}
