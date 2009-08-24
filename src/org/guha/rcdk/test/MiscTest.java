package org.guha.rcdk.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.guha.rcdk.util.Misc;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.io.IOException;

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
}
