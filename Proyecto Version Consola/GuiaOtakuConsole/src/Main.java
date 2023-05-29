import java.util.LinkedList;
import java.util.Scanner;

/**
 * Programa de recomendaciones de Anime
 * @author diego leiva, pablo orellana
 */
public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        View vista = new View();
        Security seguridad = new Security();
        EmbeddedNeo4j db = new EmbeddedNeo4j( "bolt://44.204.86.134:7687", "neo4j", "task-default-soap" );
        LinkedList<String> genres = new LinkedList<>();
        LinkedList<String> studios = new LinkedList<>();
        String genre;
        String studio;
        String anime;
        int option = 0;
        vista.printWelcome();
        vista.printLogIn();
        option = input.nextInt();
        switch (option){
            case 1:
                System.out.println("Ingrese su username: ");
                String username = input.next();
                System.out.println("Ingrese su contraseña: ");
                String password = input.next();
                if (db.checkCredentials(username,password)){
                    option = 0;
                    Usuario currentUser = new Usuario();
                    currentUser.setUsername(username);
                    vista.printWelcomeUser(currentUser);
                    vista.printHomePage();
                    option = input.nextInt();
                    switch (option){
                        case 1:
                            option = 0;
                            vista.printExplore();
                            option = input.nextInt();
                            switch (option){
                                case 1:
                                    option = 0;
                                    System.out.println("Los generos disponibles son: ");
                                    genres = db.getGenres();
                                    vista.printList(genres);
                                    vista.printSelect();
                                    option = input.nextInt();
                                    genre = vista.getByIndex(option,genres);
                                    System.out.println("Animes del genero: "+genre);
                                    vista.printList(db.getAnimesByGenre(genre));
                                    vista.printSelect();
                                    option = input.nextInt();
                                    anime = vista.getByIndex(option,db.getAnimesByGenre(genre));
                                    System.out.println("Esta es la informacion de: "+anime);
                                    vista.showAnimeInfo(db.getAnimeInfo(anime));
                                    break;
                                case 2:
                                    option = 0;
                                    System.out.println("Los estudios son: ");
                                    studios = db.getStudios();
                                    vista.printList(studios);
                                    vista.printSelect();
                                    option = input.nextInt();
                                    studio = vista.getByIndex(option,studios);
                                    System.out.println("Los animes que el estudio "+studio+" ha animado son:");
                                    vista.printList(db.getAnimesByStudio(studio));
                                    vista.printSelect();
                                    option = input.nextInt();
                                    anime = vista.getByIndex(option,db.getAnimesByStudio(studio));
                                    System.out.println("Esta es la informacion de: "+anime);
                                    vista.showAnimeInfo(db.getAnimeInfo(anime));
                                    break;
                            }
                            break;
                        case 2:
                            //preferencias
                            break;
                        case 3:
                            //recomendados
                            break;
                        case 4:
                            // mi perfil
                            break;
                        case 5:
                            //Cerrar de sesion
                            break;
                        default:
                            //por si elijen otra cosa
                            break;
                    }
                }else {
                    System.out.println("El usuario o contraseña son incorrectos");
                    System.out.println("Intentelo nuevamente");
                }
                break;
            case 2:
                System.out.println("Porfavor complete los siguientes campos");
                System.out.println("Ingrese el nombre de usuario que desea: ");
                String registerUser = input.next();
                System.out.println("Ingrese su nombre: ");
                String firstname = input.next();
                System.out.println("Ingrese su apellido: ");
                String lastname = input.next();
                System.out.println("Ingrese su contraseña: ");
                String contra = input.next();
                if (db.createUser(registerUser,contra,firstname,lastname))
                {
                    System.out.println("Usuario Creado Existosamente");
                    Usuario currentUser = new Usuario();
                    currentUser.setUsername(registerUser);
                    vista.printWelcomeUser(currentUser);
                }else System.out.println("El usuario ya existe, intente nuevamente");
                break;
            case 3:
                vista.printMessage("Gracias por utilizar LaGuiaOtaku");
                break;
            default:
                //Por si elije otra opcion
                break;
        }

    }


}