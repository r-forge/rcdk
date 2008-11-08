package org.guha.rcdk.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.guha.rcdk.util.Misc;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

public class MiscTest extends TestCase {
    String home = "/Users/rguha/";

    public void testLoadMolecules() {
        String[] fname = {home + "src/R/trunk/rcdk/data/dan001.hin",
                home + "src/R/trunk/rcdk/data/dan007.xyz",
                home + "src/R/trunk/rcdk/data/dan008.hin"};
        IAtomContainer[] acs = null;
        try {
            acs = Misc.loadMolecules(fname, true, true);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(3, acs.length);

    }
}
