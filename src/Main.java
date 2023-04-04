import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            printUsage();
            return;
        }

        String mode = args[0];
        if (!mode.equals("-d") && !mode.equals("-e")) {
            printUsage();
            return;
        }

        String key = args[1];
        if (!key.matches("[0-9]+")) {
            System.out.println("La clé doit être un entier");
            return;
        }
        int keyLength = Integer.parseInt(key);

        String initialVector = args[2];
        if (!initialVector.matches("[01]+")) {
            System.out.println("Le vecteur initiale doit être en binaire");
            return;
        }

        String fileName = args[3];
        byte[] messageBytes;
        if (mode.equals("-e")) {
            byte[] tempMessageBytes = octetToBinaryArray(readFile(fileName));
            int size = (tempMessageBytes.length % keyLength) == 0 ? tempMessageBytes.length : tempMessageBytes.length + (keyLength - (tempMessageBytes.length % keyLength));
            messageBytes = new byte[size];
            System.arraycopy(tempMessageBytes, 0, messageBytes, 0, tempMessageBytes.length);
        } else {
            messageBytes = readFile(fileName);
        }

        byte[] vectorBytes = stringToBinary(initialVector);
        byte[] encryptedMessage = new byte[messageBytes.length];

        for (int i = 0; i < messageBytes.length; i += keyLength) {
            byte[] tempVector = Arrays.copyOfRange(encrypt(vectorBytes), 0, keyLength);
            byte[] messageBlock = Arrays.copyOfRange(messageBytes, i, i + keyLength);
            for (int j = 0; j < messageBlock.length; j++) {
                int xorResult = messageBlock[j] ^ tempVector[j];
                encryptedMessage[i + j] = (byte) xorResult;
            }
            vectorBytes = pad(tempVector, initialVector.length());
        }

        if (mode.equals("-e")) {
            saveFile("text.encrypted", encryptedMessage);
        } else {
            saveFile("text.decrypted", binaryToString(encryptedMessage).getBytes());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar CBCProject.jar -d 4 1010 test.txt");
    }

    private static byte[] readFile(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            return bytes;
        }
    }

    private static void saveFile(String outputFileName, byte[] result) {
        try {
            FileOutputStream fos = new FileOutputStream(outputFileName);
            fos.write(result);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] vectorBytes) {
        byte temp = vectorBytes[0];
        for (int i = 0; i < vectorBytes.length - 1; i++) {
            vectorBytes[i] = vectorBytes[i + 1];
        }
        vectorBytes[vectorBytes.length - 1] = temp;
        return vectorBytes;
    }

    public static byte[] pad(byte[] vectorBytes, int size) {
        byte[] paddedVector = new byte[size];
        for (int i = size - vectorBytes.length; i < vectorBytes.length; i++) {
            paddedVector[i] = vectorBytes[i - (size - vectorBytes.length)];
        }
        return paddedVector;
    }
    public static byte[] octetToBinaryArray(byte[] bytes) {
        byte[] binary = new byte[bytes.length * 8];
        for (int i = 0; i < bytes.length; i++) {
            int val = bytes[i];
            for (int j = 0; j < 8; j++) {
                binary[i * 8 + j] = (byte) ((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return binary;
    }

    public static String binaryToString(byte[] binary) {
        byte[] bytes = new byte[binary.length / 8];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 0; j < 8; j++) {
                bytes[i] = (byte) ((bytes[i] << 1) | binary[i * 8 + j]);
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] stringToBinary(String binaryString) {
        byte[] result = new byte[binaryString.length()];
        for (int i = 0; i < binaryString.length(); i++) {
            result[i] = (byte) Character.getNumericValue(binaryString.charAt(i));
        }
        return result;
    }
}
