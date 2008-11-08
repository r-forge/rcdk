package org.guha.rcdk.test;

import junit.framework.TestCase;
import org.guha.rcdk.view.ViewMolecule3DDataTable;

/**
 * Created by IntelliJ IDEA.
 * User: rguha
 * Date: Aug 28, 2006
 * Time: 2:58:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class View3DDataTableTest extends TestCase {

    public void test1() {
        String[] fnames = { "/Users/rguha/src/R/trunk/rcdk/data/dan001.hin",
                "/Users/rguha/src/R/trunk/rcdk/data/dan002.hin",
                "/Users/rguha/src/R/trunk/rcdk/data/dan003.hin" };
        String[] cnames = {"Molecule", "Col1", "Col2"};
        Object[][] xvals = new Object[3][2];
        xvals[0][0] = "Hello";
        xvals[0][1] = 1.234;

        xvals[1][0] = "Hello";
        xvals[1][1] = 1.234;

        xvals[2][0] = "Hello";
        xvals[2][1] = 1.234;

        ViewMolecule3DDataTable t = new ViewMolecule3DDataTable(fnames, cnames, xvals);

        fail();

    }

   
}