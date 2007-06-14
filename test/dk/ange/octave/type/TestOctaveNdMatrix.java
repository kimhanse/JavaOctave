package dk.ange.octave.type;

import java.util.TreeMap;

import junit.framework.TestCase;
import dk.ange.octave.Octave;

/**
 * @author Esben Mose Hansen
 * 
 */
public class TestOctaveNdMatrix extends TestCase {

    /**
     * @throws Exception
     */
    public void testGetAndSet() throws Exception {
        OctaveNdMatrix matrix = new OctaveNdMatrix(3, 6, 5, 4);
        matrix.set(2.0, 2, 5, 2, 3);
        for (int row = 1; row <= 3; row++) {
            for (int column = 1; column <= 6; column++) {
                for (int depth = 1; depth <= 5; depth++) {
                    for (int coffee = 1; coffee <= 4; coffee++) {
                        if (row == 2 && column == 5 && depth == 2 && coffee == 3) {
                            assertEquals(matrix.get(row, column, depth, coffee), 2.0);
                        } else {
                            assertEquals(matrix.get(row, column, depth, coffee), 0.0);
                        }
                    }
                }
            }
        }
        try {
            matrix.get(2, 3, 1, 0);
            fail("Attempt to get with a position that includes a 0 should fail");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            matrix.get(2, 3, 10, 3);
            fail("Attempt to get with a position that includes exceeds range should fail");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }
        try {
            matrix.get(2, 3, 2, 3, 4);
            fail("Attempt to get with a position that includes exceeds dimensions should fail");
        } catch (IndexOutOfBoundsException e) {
            // ok
        }

    }

    /**
     * @throws Exception
     */
    public void testSizeConstructor() throws Exception {
        OctaveNdMatrix matrix = new OctaveNdMatrix(3, 6, 5, 4);
        assertEquals(matrix.getSize().length, 4);
        assertEquals(matrix.getSize()[0], 3);
        assertEquals(matrix.getSize()[1], 6);
        assertEquals(matrix.getSize()[2], 5);
        assertEquals(matrix.getSize()[3], 4);

        OctaveNdMatrix matrixEmpty = new OctaveNdMatrix(0, 0);
        assertEquals(matrixEmpty.getData().length, 0);

        try {
            new OctaveNdMatrix(1);
            fail("OctaveNdMatrixes does not support less than one dimension. ");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    /**
     * @throws Exception
     */
    public void testDataSizeConstructor() throws Exception {
        double[] data = new double[2 * 3 * 4];
        for (int idx = 0; idx < data.length; idx++) {
            data[idx] = idx + 1.0;
        }
        OctaveNdMatrix matrix = new OctaveNdMatrix(data, 2, 3, 4);
        double d = 1.0;
        for (int depth = 1; depth <= 4; depth++) {
            for (int column = 1; column <= 3; column++) {
                for (int row = 1; row <= 2; row++) {
                    assertEquals(d, matrix.get(row, column, depth));
                    d++;
                }
            }
        }

        try {
            new OctaveNdMatrix(data, 2, 2, 4);
            fail("data and dimenstion must match");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new OctaveNdMatrix(data, 2, 3, 5);
            fail("data and dimenstion must match");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    /**
     * @throws Exception
     */
    public void testMakeCopy() throws Exception {
        double[] data = new double[2 * 3 * 4];
        for (int idx = 0; idx < data.length; idx++) {
            data[idx] = idx + 1.0;
        }
        OctaveNdMatrix matrix = (new OctaveNdMatrix(data, 2, 3, 4)).makecopy();
        double d = 1.0;
        for (int depth = 1; depth <= 4; depth++) {
            for (int column = 1; column <= 3; column++) {
                for (int row = 1; row <= 2; row++) {
                    assertEquals(matrix.get(row, column, depth), d);
                    d++;
                }
            }
        }

    }

    /**
     * @throws Exception
     *             matrixzero doesn't work because of bug in octave
     */
    public void testSetAndGetOctave() throws Exception {
        Octave octave = new Octave();
        TreeMap<String, OctaveType> vars = new TreeMap<String, OctaveType>();
        double[] bigdata = new double[2 * 3 * 4];
        for (int idx = 0; idx < bigdata.length; idx++) {
            bigdata[idx] = idx +1.0;
        }
        double[] data2d = { 1.0, 2.0, 3.0, 5.0, 8.0, 13.0 };
        double[] datascalar = { 42.0 };
        vars.put("bigmatrix", new OctaveNdMatrix(bigdata, 1, 2, 3, 4));
        vars.put("matrix2d", new OctaveNdMatrix(data2d, 2, 3));
        vars.put("matrixscalar", new OctaveNdMatrix(datascalar, 1, 1));
        // vars.put("matrixzero", new OctaveNdMatrix(0, 0, 0, 0));
        vars.put("matrixzero2d", new OctaveNdMatrix(0, 0));
        octave.set(vars);
        // OctaveNdMatrix matrixzero = new OctaveNdMatrix(octave.get("matrixzero"));
        OctaveNdMatrix matrix2d = new OctaveNdMatrix(octave.get("matrix2d"));
        OctaveNdMatrix bigmatrix = new OctaveNdMatrix(octave.get("bigmatrix"));
        OctaveNdMatrix matrixzero2d = new OctaveNdMatrix(octave.get("matrixzero2d"));
        OctaveNdMatrix matrixscalar = new OctaveNdMatrix(octave.get("matrixscalar"));
        // assertEquals(matrixzero, vars.get("matrixzero"));
        assertEquals(matrixzero2d, vars.get("matrixzero2d"));
        assertEquals(matrixscalar, vars.get("matrixscalar"));
        assertEquals(matrix2d, vars.get("matrix2d"));
        assertEquals(bigmatrix, vars.get("bigmatrix"));

        assertEquals("" + //
                "# name: matrixzero2d\n" + //
                "# type: matrix\n" + //
                "# rows: 0\n" + //
                "# columns: 0\n\n" //
        , matrixzero2d.toText("matrixzero2d"));

        /*
         * assertEquals("" + // "# name: matrixzero\n" + // "# type: matrix\n" + // "# ndims: 4\n" + // " 0 0 0 0\n" // ,
         * matrixzero.toText("matrixzero"));
         */
        assertEquals("" + //
                "# name: matrixscalar\n" + //
                "# type: matrix\n" + //
                "# rows: 1\n" + //
                "# columns: 1\n" +//
                " 42.0\n\n" //
        , matrixscalar.toText("matrixscalar"));

        assertEquals("" + //
                "# name: matrix2d\n" + //
                "# type: matrix\n" + //
                "# rows: 2\n" + //
                "# columns: 3\n" + //
                " 1.0 3.0 8.0\n" + //
                " 2.0 5.0 13.0\n\n" //
        , matrix2d.toText("matrix2d"));

        assertEquals("" + //
                "# name: bigmatrix\n" + //
                "# type: matrix\n" + //
                "# ndims: 4\n" + //
                " 1 2 3 4\n" + //
                " 1.0\n" + //
                " 2.0\n" + //
                " 3.0\n" + //
                " 4.0\n" + //
                " 5.0\n" + //
                " 6.0\n" + //
                " 7.0\n" + //
                " 8.0\n" + //
                " 9.0\n" + //
                " 10.0\n" + //
                " 11.0\n" + //
                " 12.0\n" + //
                " 13.0\n" + //
                " 14.0\n" + //
                " 15.0\n" + //
                " 16.0\n" + //
                " 17.0\n" + //
                " 18.0\n" + //
                " 19.0\n" + //
                " 20.0\n" + //
                " 21.0\n" + //
                " 22.0\n" + //
                " 23.0\n" + //
                " 24.0\n\n" //
        , bigmatrix.toText("bigmatrix"));
        octave.close();
    }
    
    /**
     * @throws Exception
     *             matrixzero doesn't work because of bug in octave
     */
    public void testGrowth() throws Exception {
        OctaveNdMatrix matrix = new OctaveNdMatrix(3,3,3,3);
        matrix.set(42.0,2,2,2,2);
        matrix.set(1.0,3,2,2,2);
        matrix.set(2.0,2,3,2,2);
        matrix.set(3.0,2,2,3,2);
        matrix.set(4.0,2,2,2,3);
        assertEquals(42.0, matrix.get(2,2,2,2));
        assertEquals(1.0, matrix.get(3,2,2,2));
        assertEquals(2.0, matrix.get(2,3,2,2));
        assertEquals(3.0, matrix.get(2,2,3,2));
        assertEquals(4.0, matrix.get(2,2,2,3));
        
        matrix.set(Math.PI, 4,5,7,6);
        assertEquals(42.0, matrix.get(2,2,2,2));
        assertEquals(1.0, matrix.get(3,2,2,2));
        assertEquals(2.0, matrix.get(2,3,2,2));
        assertEquals(3.0, matrix.get(2,2,3,2));
        assertEquals(4.0, matrix.get(2,2,2,3));
        assertEquals(Math.PI, matrix.get(4,5,7,6));
        
        
    }
    

}
