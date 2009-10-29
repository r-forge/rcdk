package org.guha.rcdk.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.guha.rcdk.util.Misc;
import org.jmol.smiles.InvalidSmilesException;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.IOException;
import java.io.StringWriter;

public class MiscTest extends TestCase {
    String home = "/Users/rguha/";

    public void testLoadMolecules() {
        String[] fname = {home + "src/R/trunk/rcdk/data/dan001.hin",
                home + "src/R/trunk/rcdk/data/dan007.xyz",
                home + "src/R/trunk/rcdk/data/dan008.hin"};
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(fname, true, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Assert.assertEquals(3, acs.length);

    }

    public void testLoadMolsFromSmi() {
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(new String[]{"/Users/rguha/src/R/trunk/rcdk/data/big.smi"}, true, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        assert acs != null;
        Assert.assertEquals(277, acs.length);

    }

    public void testWriteMoleculesDirectly() throws InvalidSmilesException, CDKException, IOException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCCCC");
        mol.setProperty(CDKConstants.TITLE, "FooMolecule");
        mol.setProperty("foo", "bar");


        StringWriter sw = new StringWriter();
        SDFWriter writer = new SDFWriter(sw);
        writer.write(mol);
        writer.close();
        Assert.assertNotNull(sw.toString());
        Assert.assertFalse(sw.toString().equals(""));
        Assert.assertTrue(sw.toString().indexOf("FooMolecule") == 0);
        Assert.assertTrue(sw.toString().indexOf("<foo>") > 0);
    }

    public void testWriteMolecules() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCCCCC");
        mol.setProperty(CDKConstants.TITLE, "FooMolecule");
        mol.setProperty("foo", "bar");

        Misc.writeMoleculesInOneFile(new IAtomContainer[]{mol}, "/Users/rguha/foo.sdf", 0);
    }
}
