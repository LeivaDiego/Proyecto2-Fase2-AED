import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class NavigationController {
    private Scanner input;
    private View vista;
    private Security security;
    private EmbeddedNeo4j db;
    private Usuario currentUser;
    private LinkedList<String> genres; //almacena el listado de generos en la base de datos
    private LinkedList<String> studios; //almacena el listado de estudio en la base de datos
    private String genre;//almacena el nombre del genero seleccionado por el usuario
    private String anime;//almacena el nombre del anime seleccionado por el usuario
    private String studio; // almacena el estudio seleccionado por el usuario


    /**
     * Constructor del navegador
     */
    public NavigationController() {
        input = new Scanner(System.in);
        vista = new View();
        security = new Security();
        db = new EmbeddedNeo4j("bolt://44.204.86.134:7687", "neo4j", "task-default-soap");
        currentUser = new Usuario();
        studios = db.getStudios();
        genres = db.getGenres();
    }

    /**
     * Inicializa la aplicacion con la bienvenida y el menu de ingreso
     */
    public void startApplication() throws Exception {
        boolean flag = true;
        int option = 0;
        vista.printWelcome();
        while(flag) {
            if (currentUser.getUsername() != null) { // Si el usuario está logueado
                vista.userSession(currentUser);
                vista.printHomePage();
                option = security.validOption();
                switch (option){
                    case 1: //Explorar
                        exploreAnime();
                        break;
                    case 2: //preferencias
                        configurePreferences(currentUser);
                        break;
                    case 3: //recomendaciones
                        showRecommendations(currentUser);
                        break;
                    case 4: // mi usuario
                        myUser();
                        break;
                    case 5: //cerrar sesion
                        vista.Message("Has cerrado Sesión");
                        currentUser = new Usuario(); // Reiniciamos el usuario
                        break;
                    default:
                        vista.printInvalidOption();
                        break;
                }
            } else { // Si el usuario no está logueado
                vista.printEntryMenu();
                option = security.validOption();
                switch (option) {
                    case 1: // iniciar sesion
                        login();
                        break;
                    case 2: // registrarse
                        register();
                        break;
                    case 3: // salir
                        vista.Message("Gracias por utilizar LaGuiaOtaku");
                        flag = false;
                        db.close();
                        break;
                    default:
                        vista.printInvalidOption();
                        break;
                }
            }
        }
    }

    /**
     * Realiza el inicio de sesion a un usuario existente
     */
    protected void login() throws Exception {
        vista.Message("=== INICIO DE SESIÓN ===");
        vista.Message("Ingrese su nombre de usuario: ");
        String username = input.next();
        vista.Message("Ingrese su contraseña: ");
        String password = input.next();
        if (db.checkCredentials(username, password)) {
            currentUser.setUsername(username);
            //startApplication();
            //HomePage();
        } else {
            vista.printError("El usuario o contraseña son incorrectos");
            vista.Message("Intentelo nuevamente");
        }
    }

    /**
     * realiza la creacion de un usuario
     */
    protected void register() throws Exception {
        vista.Message("=== REGISTRO ===");
        vista.Message("Por favor complete los siguientes campos");
        vista.Message("Ingrese el nombre de usuario que desea: ");
        String username = input.next();
        vista.Message("Ingrese su nombre: ");
        String firstName = input.next();
        vista.Message("Ingrese su apellido: ");
        String lastName = input.next();
        vista.Message("Ingrese su contraseña: ");
        String password = input.next();

        if (db.createUser(username, password, firstName, lastName)) {
            vista.Message("Usuario creado exitosamente");
            currentUser.setUsername(username);
            //startApplication();
        } else {
            vista.printError("El usuario ya existe, intente nuevamente");
        }
    }


    /**
     * Realiza la seccion de exploracion
     */
    private void exploreAnime() throws Exception {
        int option = 0;
        boolean flag = true;
        boolean innerFlag= true;
        String temp;
        while (flag){
            vista.Separator();
            vista.printExplore();
            option = security.validOption();
            switch (option) {
                case 1:
                    System.out.println("Los géneros disponibles son: ");
                    vista.printList(genres);
                    vista.printSelect();
                    option = security.validOption();
                    genre = vista.getByIndex(option,genres);
                    vista.Message("Animes del genero: "+genre);
                    vista.printList(db.getAnimesByGenre(genre));
                    vista.Message("Selecciona el anime que quieres ver: ");
                    option = security.validOption();
                    anime = vista.getByIndex(option,db.getAnimesByGenre(genre));
                    vista.Message("Esta es la informacion de: "+anime);
                    vista.showAnimeInfo(db.getAnimeInfo(anime));
                    vista.printReturn("EXPLORAR");
                    temp = input.next();
                    break;
                case 2:
                    System.out.println("Los estudios disponibles son: ");
                    vista.printList(studios);
                    vista.printSelect();
                    option = input.nextInt();
                    studio = vista.getByIndex(option,studios);
                    vista.Message("Los animes que el estudio "+studio+" ha animado son:");
                    vista.printList(db.getAnimesByStudio(studio));
                    vista.Message("Selecciona el anime que quieres ver: ");
                    option = input.nextInt();
                    anime = vista.getByIndex(option,db.getAnimesByStudio(studio));
                    vista.Message("Esta es la informacion de: "+anime);
                    vista.showAnimeInfo(db.getAnimeInfo(anime));
                    vista.printReturn("EXPLORAR");
                    temp = input.next();
                    break;
                case 3:
                    flag = false;
                    //HomePage();
                    //startApplication();
            }
        }
    }

    /**
     * realiza la seccion de preferencias
     * @param currentUser el username del usuario de sesion actual
     */
    private void configurePreferences(Usuario currentUser) throws Exception {
        vista.Separator();
        vista.Message("=== PREFERENCIAS ===");
        String temp;
        if (db.userHasInterests(currentUser.getUsername())){
            int option = 0;
            vista.Message("Actualmente ya tienes estas preferencias: ");
            vista.Message("Géneros: ");
            vista.printList(db.getUserInterestsGenres(currentUser.getUsername()));
            vista.Message("Estudios: ");
            vista.printList(db.getUserInterestsStudios(currentUser.getUsername()));
            vista.Message("¿Deseas resetear tus preferencias?");
            vista.Message("1. Sí");
            vista.Message("2. No");
            vista.printSelect();
            option = security.validOption();
            if (option == 1){
                vista.Message("Estas seguro de borrar tus preferencias?");
                vista.Message("Esto borrara tus preferencias acutales");
                vista.Message("1. Sí");
                vista.Message("2. No");
                option = security.validOption();
                if (option == 1){
                    db.resetInterests(currentUser.getUsername());
                    vista.Message("Tus preferencias se han borrado");
                }
            }
            vista.printReturn("MENU PRINCIPAL");
            temp = input.next();
            //HomePage();
            //startApplication();
        } else {
            System.out.println("¡Te invitamos a que selecciones tus categorías de interés!");
            System.out.println("GÉNEROS");
            System.out.println("Selecciona los que más te gusten");
            vista.printList(genres);
            List<String> genreInterest = security.validFormatPref(genres);
            System.out.println("Excelente ahora la siguiente categoría");
            System.out.println("ESTUDIOS");
            System.out.println("Selecciona los que más te gusten");
            vista.printList(studios);
            List<String> studioInterest = security.validFormatPref(studios);
            System.out.println("Tus preferencias se han guardado exitosamente");
            db.createInterests(currentUser.getUsername(), genreInterest, studioInterest);
            vista.printReturn("MENU PRINCIPAL");
            temp = input.next();
            //HomePage();
            //startApplication();
        }
    }

    /**
     * realiza la seccion de recomendaciones
     * @param currentUser el username del usuario de sesion actual
     */
    private void showRecommendations(Usuario currentUser) throws Exception {
        String temp;
        vista.Separator();
        vista.Message("=== RECOMENDACIONES ===");
        if (db.userHasInterests(currentUser.getUsername())){
            vista.Message("Estas son todos los animes que te recomendamos");
            vista.Message("Basados en tus preferencias actuales");
            ArrayList<String> recommended = db.createMegaList(currentUser.getUsername());
            if (recommended.isEmpty()){
                vista.Message("Parece que no tenemos animes para ti");
            }else {
                for (int i = 0; i < recommended.size(); i++) {
                    String item = recommended.get(i);
                    System.out.println(i + ". " + item);
                }
            }
            vista.printReturn("MENU PRINCIPAL");
            temp = input.next();
            //HomePage();
            //startApplication();
        } else {
            System.out.println("Vaya, parece que aún no has configurado tus preferencias");
            //HomePage();
            //startApplication();
        }
    }


    /**
     * Realiza la seccion de mi usuario
     */
    private void myUser(){
        vista.Separator();
        String temp;
        vista.Message("=== MI PERFIL ===");
        vista.showUserInfo(db.getUserInfo(currentUser.getUsername()));
        vista.printReturn("MENU PRINCIPAL");
        temp=input.next();
    }

}
