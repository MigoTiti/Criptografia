package criptografia.criptografia;

import java.util.ArrayList;
import java.util.Random;

public class Criptografia {

    private static final int N = 8;

    public static void main(String[] args) {
        ArrayList<Integer> chavePrivada = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            adicionarNumeroSuperInc(chavePrivada);
        }

        int q = soma(chavePrivada) + (new Random().nextInt(90) + 3);
        int r;

        do {
            r = new Random().nextInt(q + 1) + 1;
        } while (MDC(q, r) != 1);

        ArrayList<Integer> chavePublica = gerarChavePublica(chavePrivada, q, r);
        ArrayList<Integer> codigosCrip = new ArrayList<>();

        String mensagem = "teste";
        StringBuilder mensFinal = new StringBuilder();

        for (int i = 0; i < mensagem.length(); i++) {
            if (mensagem.charAt(i) != ' ') {
                int mensCrip = criptografar(String.valueOf(mensagem.charAt(i)), chavePublica);
                codigosCrip.add(mensCrip);
            } else
                codigosCrip.add(0);
        }

        for (Integer aCodigosCrip : codigosCrip) {
            if (aCodigosCrip != 0)
                mensFinal.append(descriptografar(aCodigosCrip, r, q, chavePrivada));
            else
                mensFinal.append(" ");
        }

        System.out.println(mensFinal.toString());
    }

    private static int criptografar(String mens, ArrayList<Integer> chavePublica) {
        byte[] binario = mens.getBytes();

        ArrayList<Integer> binary = new ArrayList<>();
        for (byte b : binario) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.add((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }

        while (binary.size() < Criptografia.N) {
            binary.add(0, 0);
        }

        int charCrip = binary.get(0) * chavePublica.get(0);
        for (int i = 1; i < binary.size(); i++) {
            charCrip += binary.get(i) * chavePublica.get(i);
        }

        return charCrip;
    }

    private static String descriptografar(int mensCrip, int r, int q, ArrayList<Integer> chavePrivada) {
        int r2 = 0;
        do {
            r2++;
        } while ((r2 * r % q) != 1);

        int[] chaveAux = new int[chavePrivada.size()];
        for (int i = 0; i < chavePrivada.size(); i++) {
            chaveAux[i] = chavePrivada.get(i);
        }

        int[] baldeAux = binPack(chaveAux, r2 * mensCrip % q);

        String mensagemDec = "";
        for (int aBaldeAux : baldeAux) {
            mensagemDec += aBaldeAux;
        }

        int charCodigo = Integer.parseInt(mensagemDec, 2);

        return Character.toString((char) charCodigo);
    }

    private static int[] binPack(int[] elementos, int limiteBalde) {
        int nElementos = elementos.length;
        int usado = 0;
        int[] balde = new int[nElementos];

        quickSortDecres(elementos, 0, nElementos - 1);

        for (int item = 0; item < nElementos; item++) {
            int binLoc = 0;
            if (usado + elementos[item] <= limiteBalde) {
                binLoc = 1;
                usado += elementos[item];
            }
            balde[item] = binLoc;
        }

        int[] baldeAux = new int[balde.length];
        for (int i = 0; i < balde.length; i++) {
            baldeAux[i] = balde[balde.length - 1 - i];
        }

        return baldeAux;
    }

    private static ArrayList<Integer> gerarChavePublica(ArrayList<Integer> chavePrivada, int q, int r) {
        ArrayList<Integer> chavePublica = new ArrayList<>();

        for (Integer chave : chavePrivada) {
            chavePublica.add((chave * r) % q);
        }

        return chavePublica;
    }

    private static int soma(ArrayList<Integer> vetor) {
        int soma = 0;

        for (Integer elemento : vetor) {
            soma += elemento;
        }

        return soma;
    }

    private static int MDC(int a, int b) {
        int resto;

        while (b != 0) {
            resto = a % b;
            a = b;
            b = resto;
        }

        return a;
    }

    private static void adicionarNumeroSuperInc(ArrayList<Integer> chave) {
        int soma = 0;
        if (chave.size() > 0) {
            for (Integer aChave : chave) {
                soma += aChave;
            }
        }
        chave.add(Math.abs(soma) + (new Random().nextInt(5) + 1));
    }

    private static void quickSortDecres(int A[], int p, int r) {
        if (p < r) {
            int q = partitionDecres(A, p, r);
            quickSortDecres(A, p, q - 1);
            quickSortDecres(A, q + 1, r);
        }
    }

    private static int partitionDecres(int A[], int p, int r) {
        int i = p - 1;
        swap(A, (r + p) / 2, r);
        int x = A[r];
        for (int j = p; j < r; j++) {
            if (A[j] > x) {
                i++;
                swap(A, i, j);
            }
        }
        swap(A, i + 1, r);
        return i + 1;
    }

    private static void swap(int[] vetor, int j, int aposJ) {
        int aux = vetor[j];
        vetor[j] = vetor[aposJ];
        vetor[aposJ] = aux;
    }
}
