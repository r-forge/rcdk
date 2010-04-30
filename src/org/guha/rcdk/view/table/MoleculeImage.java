package org.guha.rcdk.view.table;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.guha.rcdk.util.Misc;
import org.guha.rcdk.view.panels.MoleculeCell;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;

import sun.awt.image.PNGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.objectplanet.image.PngEncoder;

/**
 * A one line summary.
 *
 * @author Rajarshi Guha
 */
public class MoleculeImage  {
    private MoleculeCell moleculeCell;
    private IAtomContainer molecule;
    private int width, height;

    public MoleculeImage(IAtomContainer molecule) throws Exception {
        this(molecule, 300, 300);
    }

    public MoleculeImage(IAtomContainer molecule, int width, int height) throws Exception {
        if (!ConnectivityChecker.isConnected(molecule)) throw new CDKException("Molecule must be connected");
        molecule = AtomContainerManipulator.removeHydrogens(molecule);
        try {
            CDKHueckelAromaticityDetector.detectAromaticity(molecule);
        } catch (CDKException e) {
            throw new Exception("Error in aromatcity detection");
        }
        this.molecule = Misc.getMoleculeWithCoordinates(molecule);
        moleculeCell = new MoleculeCell(molecule, width, height);
        this.width = width;
        this.height = height;
    }

    public byte[] getBytes() throws IOException {

        Rectangle drawArea = new Rectangle(width, height);
        Image image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        List<IGenerator> generators = new ArrayList<IGenerator>();
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());

        // the renderer needs to have a toolkit-specific font manager
        Renderer renderer = new Renderer(generators, new AWTFontManager());

        // the call to 'setup' only needs to be done on the first paint
        renderer.setup(molecule, drawArea);

        // paint the background
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // the paint method also needs a toolkit-specific renderer
        renderer.paintMolecule(molecule, new AWTDrawVisitor(g2), drawArea, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        (new PngEncoder()).encode(image, baos);

        return baos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1CC(=O)C1COCNC1");

        MoleculeImage mi = new MoleculeImage(mol, 300,300);
        byte[] bytes = mi.getBytes();
        FileOutputStream fos = new FileOutputStream("test.png");
        fos.write(bytes);
    }
}
