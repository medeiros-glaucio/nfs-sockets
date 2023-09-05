package br.edu.ifpb.gugawag.so.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor2 {

    // ~ declarando a lista sugerida
    private static List<String> arquivos = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("== Servidor ==");

        // Configurando o socket
        ServerSocket serverSocket = new ServerSocket(7001);
        Socket socket = serverSocket.accept();

        // pegando uma referência do canal de saída do socket. Ao escrever nesse canal, está se enviando dados para o
        // servidor
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // pegando uma referência do canal de entrada do socket. Ao ler deste canal, está se recebendo os dados
        // enviados pelo servidor
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // laço infinito do servidor
        while (true) {
            System.out.println("Cliente: " + socket.getInetAddress());

//            String mensagem = dis.readUTF();
//            System.out.println(mensagem);

//            dos.writeUTF("Li sua mensagem: " + mensagem);

            // ~ ler e implementar a operação do cliente, por último enviar resposta
            String op = dis.readUTF();
            String resposta = implementarOp(op);
            dos.writeUTF(resposta);
        }
        /*
         * Observe o while acima. Perceba que primeiro se lê a mensagem vinda do cliente (linha 29, depois se escreve
         * (linha 32) no canal de saída do socket. Isso ocorre da forma inversa do que ocorre no while do Cliente2,
         * pois, de outra forma, daria deadlock (se ambos quiserem ler da entrada ao mesmo tempo, por exemplo,
         * ninguém evoluiria, já que todos estariam aguardando.
         */

    }

    private static String implementarOp(String op) {
        String[] partes = op.split(" ");
        String operacao = partes[0];

        if (operacao.equals("readdir")) {
            return listarArquivos();
        } else if (operacao.equals("rename")) {
            if (partes.length != 3) {
                return "Erro: uso correto é 'rename arquivo_antigo novo_nome'";
            }
            String arquivoAntigo = partes[1];
            String novoNome = partes[2];
            return renomearArquivo(arquivoAntigo, novoNome);
        } else if (operacao.equals("create")) {
            if (partes.length != 2) {
                return "Erro: uso correto é 'create novo_arquivo'";
            }
            String novoArquivo = partes[1];
            return criarArquivo(novoArquivo);
        } else if (operacao.equals("remove")) {
            if (partes.length != 2) {
                return "Erro: uso correto é 'remove arquivo'";
            }
            String arquivo = partes[1];
            return removerArquivo(arquivo);
        } else {
            return "Comando inválido";
        }
    }

    private static String listarArquivos() {
        return String.join("\n", arquivos);
    }

    private static String renomearArquivo(String arquivoAntigo, String novoNome) {
        if (arquivos.contains(arquivoAntigo)) {
            arquivos.remove(arquivoAntigo);
            arquivos.add(novoNome);
            return "Arquivo renomeado com sucesso.";
        } else {
            return "Arquivo não encontrado.";
        }
    }

    private static String criarArquivo(String novoArquivo) {
        if (!arquivos.contains(novoArquivo)) {
            arquivos.add(novoArquivo);
            return "Arquivo criado com sucesso.";
        } else {
            return "O arquivo já existe!";
        }
    }

    private static String removerArquivo(String arquivo) {
        if (arquivos.contains(arquivo)) {
            arquivos.remove(arquivo);
            return "Arquivo removido com sucesso.";
        } else {
            return "Arquivo não encontrado.";
        }
    }
}
