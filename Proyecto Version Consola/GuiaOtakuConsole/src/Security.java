import java.util.*;

public class Security {
    private static final int SHIFT = 3; // cantidad de desplazamiento para el cifrado de César

    public boolean isLessThanEightChars(String str) {
        return str.length() < 8;
    }
    public static String encrypt(String plaintext) {
        StringBuilder cipherText = new StringBuilder();

        for (char c : plaintext.toCharArray()) {
            if (Character.isUpperCase(c)) {
                char encryptedChar = (char) ((c + SHIFT - 'A') % 26 + 'A');
                cipherText.append(encryptedChar);
            } else if (Character.isLowerCase(c)) {
                char encryptedChar = (char) ((c + SHIFT - 'a') % 26 + 'a');
                cipherText.append(encryptedChar);
            } else {
                cipherText.append(c); // no alterar otros caracteres
            }
        }

        return cipherText.toString();
    }

    public String decrypt(String cipherText) {
        StringBuilder plaintext = new StringBuilder();

        for (char c : cipherText.toCharArray()) {
            if (Character.isUpperCase(c)) {
                char decryptedChar = (char) ((c - SHIFT - 'A' + 26) % 26 + 'A');
                plaintext.append(decryptedChar);
            } else if (Character.isLowerCase(c)) {
                char decryptedChar = (char) ((c - SHIFT - 'a' + 26) % 26 + 'a');
                plaintext.append(decryptedChar);
            } else {
                plaintext.append(c); // no alterar otros caracteres
            }
        }

        return plaintext.toString();
    }

    /**
     * Metodo que verifica que las opciones de preferencias si son validas
     * @param inputList
     * @return
     */
    public List<String> validFormatPref(LinkedList<String> inputList) {
        Scanner scanner = new Scanner(System.in);
        List<String> numerosValidos = new LinkedList<>();
        boolean numerosCorrectos = false;

        while (!numerosCorrectos) {
            System.out.print("Ingrese una cadena de 3 números separados por comas (por ejemplo, '1,2,3'): ");
            String numeros = scanner.nextLine();
            String[] numerosArray = numeros.split(",");

            // Verificar formato
            if (numerosArray.length != 3) {
                System.out.println("El formato de entrada no es válido. Por favor, ingrese tres números separados por comas.");
                continue;
            }

            //verificar existencia y repetición
            numerosCorrectos = true;
            Set<Integer> numerosSet = new HashSet<>();
            for (String numero : numerosArray) {
                try {
                    int numeroInt = Integer.parseInt(numero);
                    if (inputList.size() <= numeroInt || !numerosSet.add(numeroInt)){
                        System.out.println("Uno o más números no son válidos o están repetidos. Inténtelo nuevamente.");
                        numerosCorrectos = false;
                        break;
                    }
                    numerosValidos.add(inputList.get(numeroInt));
                } catch (NumberFormatException e) {
                    System.out.println("Uno o más números no son válidos. Inténtelo nuevamente.");
                    numerosCorrectos = false;
                    break;
                }
            }
        }

        return numerosValidos;
    }

}
