package com.uniovi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.uniovi.entities.Offer;
import com.uniovi.entities.User;
import com.uniovi.service.InsertSampleDataService;
import com.uniovi.service.OffersService;
import com.uniovi.service.RolesService;
import com.uniovi.service.UsersService;
import com.uniovi.tests.pageobjects.PO_AddOfferView;
import com.uniovi.tests.pageobjects.PO_HomeView;
import com.uniovi.tests.pageobjects.PO_LoginView;
import com.uniovi.tests.pageobjects.PO_MyOffers;
import com.uniovi.tests.pageobjects.PO_NavView;
import com.uniovi.tests.pageobjects.PO_OffersPurchased;
import com.uniovi.tests.pageobjects.PO_Properties;
import com.uniovi.tests.pageobjects.PO_RegisterView;
import com.uniovi.tests.pageobjects.PO_RemoveUsersView;
import com.uniovi.tests.pageobjects.PO_View;
import com.uniovi.tests.pageobjects.PO_ViewOffers;
import com.uniovi.tests.util.SeleniumUtils;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyWallapopTest {

	// En Windows (Debe ser la versión 65.0.1 y desactivar las actualizacioens
	// automáticas)):
	static String PathFirefox64 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver022 = "C:\\Users\\Christian\\Desktop\\Labs\\SDI\\Selenium\\PL-SDI-Sesión5-material\\PL-SDI-Sesion5-material\\geckodriver022win64.exe";
	// En MACOSX (Debe ser la versión 65.0.1 y desactivar las actualizacioens
	// automáticas):
	// static String PathFirefox65 =
	// "/Applications/Firefox.app/Contents/MacOS/firefox-bin";
	// static String Geckdriver024 = "/Users/delacal/selenium/geckodriver024mac";
	// Común a Windows y a MACOSX
	static WebDriver driver = getDriver(PathFirefox64, Geckdriver022);

	static String URLlocal = "http://localhost:8090";
	static String URLremota = "http://urlsdispring:xxxx";
	static String URL = URLlocal; // Se va a probar con la URL remota, sino URL=URLlocal

	@Autowired
	private UsersService usersService;
	@Autowired
	private RolesService rolesService;
	@Autowired
	private OffersService offerService;

	@Autowired
	private InsertSampleDataService serviceDatos;

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	@Before
	public void setUp() {
		initdb();
		driver.navigate().to(URL);
	}

	public void initdb() {
		serviceDatos.removeAll();
		serviceDatos.init();
	}

	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	// Antes de la primera prueba
	@BeforeClass
	static public void begin() {
	}

	// Al finalizar la última prueba
	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	// PR1_1:Registro de Usuario con datos válidos.
	@Test
	public void PR01() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", 2, "class", "btn btn-primary");

		// Rellenamos el formulario.
		PO_RegisterView.fillForm(driver, "josefo@email.com", "Josefo", "Perez", "77777", "77777");

		// Comprobamos que entramos en la sección privada
		PO_View.checkElement(driver, "id", "profile_title");
	}

	// PR1_2:Registro de Usuario con datos inválidos (email vacío, nombre vacío,
	// apellidos vacíos).
	@Test
	public void PR02() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", 2, "class", "btn btn-primary");

		// Rellenamos el formulario con email vacío
		PO_RegisterView.fillForm(driver, " ", "Josefo", "Perez", "77777", "77777");
		// Comprobamos el error del email vacío.
		PO_RegisterView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());

		// Rellenamos el formulario con nombre vacío
		PO_RegisterView.fillForm(driver, "josefo@email.com", " ", "Perez", "77777", "77777");
		// Comprobamos el error del nombre vacío.
		PO_RegisterView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());

		// Rellenamos el formulario con apellidos vacío
		PO_RegisterView.fillForm(driver, "josefo@email.com", "Josefo", " ", "77777", "77777");
		// Comprobamos el error del apellidos vacío.
		PO_RegisterView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());

	}

	// Registro de Usuario con datos inválidos (repetición de contraseña inválida)
	@Test
	public void PR03() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", 2, "class", "btn btn-primary");

		// Rellenamos el formulario con contraseñas que no coinciden
		PO_RegisterView.fillForm(driver, "josefo@email.com", "Josefo", "Perez", "123456", "654321");
		// Comprobamos el error de las 2 contraseñas diferentes
		PO_RegisterView.checkKey(driver, "Error.signup.passwordConfirm.coincidence", PO_Properties.getSPANISH());

	}

	// Registro de Usuario con datos inválidos (email existente).
	@Test
	public void PR04() {
		// Vamos al formulario de registro
		PO_HomeView.clickOption(driver, "signup", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_RegisterView.fillForm(driver, "christian@email.com", "Josefo", "Perez", "123456", "123456");

		// COmprobamos el error de las 2 contraseñas diferentes
		PO_RegisterView.checkKey(driver, "Error.signup.email.duplicate", PO_Properties.getSPANISH());
	}

	// Inicio de sesión con datos válidos (administrador)
	@Test
	public void PR05() {
		// Vamos al formulario de inicio de sesion
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "admin@email.com", "admin");

		// Comprobamos que hemos entrado
		PO_View.checkElement(driver, "id", "profile_title");
	}

	// Inicio de sesión con datos válidos (usuario estándar).
	@Test
	public void PR06() {
		// Vamos al formulario de inicio de sesion
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Comprobamos que hemos entrado
		PO_View.checkElement(driver, "id", "profile_title");
	}

	// Inicio de sesión con datos inválidos (usuario estándar, campo email y
	// contraseña vacíos)
	@Test
	public void PR07() {
		// Vamos al formulario de inicio de sesion
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, " ", "123456");

		// Comprobamos que vemos el error
		PO_View.checkElement(driver, "id", "error_login");

	}

	// Inicio de sesión con datos válidos (usuario estándar, email existente, pero
	// contraseña
	// incorrecta).
	@Test
	public void PR08() {
		// Vamos al formulario de inicio de sesion
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "christian@email.com", "qwertyuio");

		// Comprobamos que vemos el error
		PO_View.checkElement(driver, "id", "error_login");
	}

	// Inicio de sesión con datos inválidos (usuario estándar, email no existente en
	// la aplicación).
	@Test
	public void PR09() {
		// Vamos al formulario de inicio de sesion
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "qwert@email.com", "qwertyu");

		// Comprobamos que vemos el error
		PO_View.checkElement(driver, "id", "error_login");
	}

	// Hacer click en la opción de salir de sesión y comprobar que se redirige a la
	// página de inicio de sesión (Login)
	@Test
	public void PR10() {
		// Vamos al formulario de inicio de sesion
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");

		// Rellenamos el formulario
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Pinchamos en la opción del menú del perfil del usuario
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'profile-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón desconectar
		elementos = PO_View.checkElement(driver, "id", "btn_logout");
		elementos.get(0).click();

		// Comprobamos que estamos en la página de login
		PO_View.checkElement(driver, "id", "login_title");
	}

	// Comprobar que el botón cerrar sesión no está visible si el usuario no está
	// autenticado.
	@Test
	public void PR11() {
		assertFalse(PO_View.elementExists(driver, "perfil-menu"));
	}

	// Mostrar el listado de usuarios y comprobar que se muestran todos los que
	// existen en el sistema.
	@Test
	public void PR12() {
		// Conseguir usuarios de la base de datos
		List<User> usuarios = usersService.getUsers();

		// Entramos como administrador
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "admin@email.com", "admin");

		// Seleccionamos la gestión de usuarios
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'users-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de usuarios
		elementos = PO_View.checkElement(driver, "id", "btn_userlist");
		elementos.get(0).click();

		// conseguimos las columnas con las listas de usuarios
		elementos = PO_View.checkElement(driver, "@name", "dni_value");

		List<String> emails = new ArrayList<String>();

		// obtenemos los emails
		for (int i = 0; i < elementos.size(); i++) {
			emails.add(elementos.get(i).getText());
		}

		// comprobamos que todos los dnis son de un usuario
		for (User usuario : usuarios) {
			assertTrue(emails.contains(usuario.getEmail()));
		}

		// hay igual numero de dnis y usuarios
		assertTrue(usuarios.size() == emails.size());
	}

	// Ir a la lista de usuarios, borrar el primer usuario de la lista, comprobar
	// que la lista se actualiza y dicho usuario desaparece
	@Test
	public void PR13() {
		// Conseguir usuarios de la base de datos antes de la eliminación
		List<User> usuariosAntes = usersService.getUsers();

		// Entramos como administrador
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "admin@email.com", "admin");

		// Seleccionamos la gestión de usuarios
		List<WebElement> elementosCheck = PO_View.checkElement(driver, "free", "//li[contains(@id,'users-menu')]/a");
		elementosCheck.get(0).click();

		// Pinchamos en el botón ver lista de usuarios
		elementosCheck = PO_View.checkElement(driver, "id", "btn_removeUsers");
		elementosCheck.get(0).click();

		// conseguimos el usuario que vamos a borrar
		String emailBorar = PO_RemoveUsersView.getEmailUserByPos(driver, 0);
		User userBorrar = usersService.getUserByEmail(emailBorar);

		int numUsersView = PO_RemoveUsersView.numUsers(driver);

		// Borramos la primera posición
		List<Integer> posicionesBorrar = new ArrayList<Integer>();
		posicionesBorrar.add(0);

		PO_RemoveUsersView.removeUsers(driver, posicionesBorrar);

		List<User> usuariosDespues = usersService.getUsers();

		// EL usuario no esta en bbdd
		assertFalse(usuariosDespues.contains(userBorrar));

		assertEquals(usuariosAntes.size() - 1, usuariosDespues.size());

		assertTrue(numUsersView - 1 == PO_RemoveUsersView.numUsers(driver));
	}

	// Ir a la lista de usuarios, borrar el último usuario de la lista, comprobar
	// que la lista se actualiza y dicho usuario desaparece
	@Test
	public void PR14() {
		// Conseguir usuarios de la base de datos
		List<User> usuariosAntes = usersService.getUsers();

		// Entramos como administrador
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "admin@email.com", "admin");

		// Seleccionamos la gestión de usuarios
		List<WebElement> elementosCheck = PO_View.checkElement(driver, "free", "//li[contains(@id,'users-menu')]/a");
		elementosCheck.get(0).click();

		// Pinchamos en el botón ver lista de usuarios
		elementosCheck = PO_View.checkElement(driver, "id", "btn_removeUsers");
		elementosCheck.get(0).click();

		// conseguimos el usuario que vamos a borrar
		int numUsersView = PO_RemoveUsersView.numUsers(driver);

		String emailBorar = PO_RemoveUsersView.getEmailUserByPos(driver, numUsersView - 1);
		User userBorrar = usersService.getUserByEmail(emailBorar);

		// Borramos la ultima posición
		List<Integer> posicionesBorrar = new ArrayList<Integer>();
		posicionesBorrar.add(numUsersView - 1);

		PO_RemoveUsersView.removeUsers(driver, posicionesBorrar);

		List<User> usuariosDespues = usersService.getUsers();

		// EL usuario no esta en bbdd
		assertFalse(usuariosDespues.contains(userBorrar));

		assertTrue(usuariosAntes.size() - 1 == usuariosDespues.size());

		assertTrue(numUsersView - 1 == PO_RemoveUsersView.numUsers(driver));
	}

	// Ir a la lista de usuarios, borrar 3 usuarios, comprobar que la lista se
	// actualiza y dichos usuarios desaparecen
	@Test
	public void PR15() {
		// Conseguir usuarios de la base de datos
		List<User> usuariosAntes = usersService.getUsers();

		// Entramos como administrador
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "admin@email.com", "admin");

		// Seleccionamos la gestión de usuarios
		List<WebElement> elementosCheck = PO_View.checkElement(driver, "free", "//li[contains(@id,'users-menu')]/a");
		elementosCheck.get(0).click();

		// Pinchamos en el botón ver lista de usuarios
		elementosCheck = PO_View.checkElement(driver, "id", "btn_removeUsers");
		elementosCheck.get(0).click();

		int numUsersView = PO_RemoveUsersView.numUsers(driver);

		// Borramos
		List<Integer> posicionesBorrar = new ArrayList<Integer>();
		posicionesBorrar.add(0);
		posicionesBorrar.add(1);
		posicionesBorrar.add(2);
		PO_RemoveUsersView.removeUsers(driver, posicionesBorrar);

		List<User> usuariosDespues = usersService.getUsers();

		assertTrue(usuariosAntes.size() - 3 == usuariosDespues.size());

		assertTrue(numUsersView - 3 == PO_RemoveUsersView.numUsers(driver));
	}

	// Ir al formulario de alta de oferta, rellenarla con datos válidos y pulsar el
	// botón Submit. Comprobar que la oferta sale en el listado de ofertas de dicho
	// usuario
	@Test
	public void PR16() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos la gestión de ofertas
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón añadir oferta
		elementos = PO_View.checkElement(driver, "id", "btn_addOffer");
		elementos.get(0).click();

		// Añadimos la oferta
		PO_AddOfferView.fillForm(driver, "Botella", "Botella de agua", 500.17);

		// Obtenemos todas las ofertas
		List<Offer> ofertas = PO_MyOffers.listMyOffers(driver);

		assertEquals(6, ofertas.size());

		assertEquals("Botella", ofertas.get(5).getTitle());
		assertEquals("Botella de agua", ofertas.get(5).getDescription());
		assertEquals("500.17", ofertas.get(5).getPrice().toString());
	}

	// Ir al formulario de alta de oferta, rellenarla con datos inválidos (campo
	// título vacío) y pulsar el botón Submit. Comprobar que se muestra el mensaje
	// de campo obligatorio.
	@Test
	public void PR17() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos la gestión de ofertas
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón añadir oferta
		elementos = PO_View.checkElement(driver, "id", "btn_addOffer");
		elementos.get(0).click();

		PO_AddOfferView.fillForm(driver, " ", "Botella de agua", 500.17);
		// Comprobamos el error del titulo vacío.
		PO_AddOfferView.checkKey(driver, "Error.empty", PO_Properties.getSPANISH());

		// Seleccionamos la gestión del perfil
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas publicadas
		elementos = PO_View.checkElement(driver, "id", "btn_myOffers");
		elementos.get(0).click();

		List<Offer> ofertas = PO_MyOffers.listMyOffers(driver);

		assertEquals(5, ofertas.size());// Inicializamos la bbdd con 6 ofertas
	}

	// Mostrar el listado de ofertas para dicho usuario y comprobar que se muestran
	// todas los que existen para este usuario
	@Test
	public void PR18() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos la gestion del area personal
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas propias
		elementos = PO_View.checkElement(driver, "id", "btn_myOffers");
		elementos.get(0).click();

		// Obtenemos las ofertas que se ven en la vista
		List<Offer> ofertasVista = PO_MyOffers.listMyOffers(driver);

		List<Offer> ofertasBBDD = offerService.getOffersForUser(usersService.getUserByEmail("christian@email.com"));

		assertEquals(ofertasBBDD.size(), ofertasVista.size());
	}

	// Ir a la lista de ofertas, borrar la primera oferta de la lista, comprobar que
	// la lista se actualiza y que la oferta desaparece
	@Test
	public void PR19() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos la gestion del area personal
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas propias
		elementos = PO_View.checkElement(driver, "id", "btn_myOffers");
		elementos.get(0).click();

		// Obtenemos las ofertas que se ven en la vista antes de borrar
		List<Offer> ofertasVistaAntes = PO_MyOffers.listMyOffers(driver);

		PO_MyOffers.removeOffer(driver, 0);

		// Obtenemos las ofertas que se ven en la vista antes de borrar
		List<Offer> ofertasVistaDespues = PO_MyOffers.listMyOffers(driver);
		List<Offer> ofertasBBDD = offerService.getOffersForUser(usersService.getUserByEmail("christian@email.com"));

		assertEquals(ofertasVistaAntes.size() - 1, ofertasVistaDespues.size());
		assertEquals(ofertasVistaDespues.size(), ofertasBBDD.size());
		assertFalse(ofertasBBDD.contains(ofertasVistaAntes.get(0)));
	}

	// Ir a la lista de ofertas, borrar la última oferta de la lista, comprobar que
	// la lista se actualiza y que la oferta desaparece
	@Test
	public void PR20() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos la gestion del area personal
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas propias
		elementos = PO_View.checkElement(driver, "id", "btn_myOffers");
		elementos.get(0).click();

		// Obtenemos las ofertas que se ven en la vista antes de borrar
		List<Offer> ofertasVistaAntes = PO_MyOffers.listMyOffers(driver);

		PO_MyOffers.removeOffer(driver, ofertasVistaAntes.size() - 1);

		// Obtenemos las ofertas que se ven en la vista antes de borrar
		List<Offer> ofertasVistaDespues = PO_MyOffers.listMyOffers(driver);
		List<Offer> ofertasBBDD = offerService.getOffersForUser(usersService.getUserByEmail("christian@email.com"));

		assertEquals(ofertasVistaAntes.size() - 1, ofertasVistaDespues.size());
		assertEquals(ofertasVistaDespues.size(), ofertasBBDD.size());
		assertFalse(ofertasBBDD.contains(ofertasVistaAntes.get(ofertasVistaAntes.size() - 1)));
	}

	// Hacer una búsqueda con el campo vacío y comprobar que se muestra la página
	// que corresponde con el listado de las ofertas existentes en el sistema
	@Test
	public void PR21() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas disponibles
		elementos = PO_View.checkElement(driver, "id", "btn_viewOffers");
		elementos.get(0).click();

		PO_ViewOffers.search(driver, "");
		List<String> nombreOfertas = PO_ViewOffers.listNameOffers(driver);
		List<Offer> ofertasBBDD = offerService.getAllOffers();

		assertEquals(nombreOfertas.size(), ofertasBBDD.size());
		List<String> nombresBBD = new ArrayList<String>();
		for (Offer oferta : ofertasBBDD) {
			nombresBBD.add(oferta.getTitle());
		}

		for (String nombre : nombreOfertas) {
			assertTrue(nombresBBD.contains(nombre));
		}

	}

	// Hacer una búsqueda escribiendo en el campo un texto que no exista y comprobar
	// que se muestra la página que corresponde, con la lista de ofertas vacía.
	@Test
	public void PR22() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas disponibles
		elementos = PO_View.checkElement(driver, "id", "btn_viewOffers");
		elementos.get(0).click();

		PO_ViewOffers.search(driver, "dsjvgsojhfbsdojf");
		assertFalse(PO_ViewOffers.elementExistsName(driver, "title_value"));

	}

	/*
	 * Sobre una búsqueda determinada (a elección de desarrollador), comprar una
	 * oferta que deja un saldo positivo en el contador del comprobador. Y comprobar
	 * que el contador se actualiza correctamente en la vista del comprador.
	 */
	@Test
	public void PR23() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas disponibles
		elementos = PO_View.checkElement(driver, "id", "btn_viewOffers");
		elementos.get(0).click();

		PO_ViewOffers.purchaseOffer(driver, "GE2");

		// Comprobamos el saldo
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'profile-menu')]/a");
		elementos.get(0).click();
		elementos = PO_View.checkElement(driver, "id", "saldo");
		assertEquals("93.0€", elementos.get(0).getText()); // 100€-7€
	}

	/*
	 * Sobre una búsqueda determinada (a elección de desarrollador), comprar una
	 * oferta que deja un saldo 0 en el contador del comprobador. Y comprobar que el
	 * contador se actualiza correctamente en la vista del comprador.
	 */
	@Test
	public void PR24() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas disponibles
		elementos = PO_View.checkElement(driver, "id", "btn_viewOffers");
		elementos.get(0).click();

		PO_ViewOffers.purchaseOffer(driver, "NO3");

		// Comprobamos el saldo
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'profile-menu')]/a");
		elementos.get(0).click();
		elementos = PO_View.checkElement(driver, "id", "saldo");
		assertEquals("0.0€", elementos.get(0).getText()); // 100€-100€
	}

	/*
	 * Sobre una búsqueda determinada (a elección de desarrollador), intentar comprar una oferta
que esté por encima de saldo disponible del comprador. Y comprobar que se muestra el mensaje de
saldo no suficiente.
	 */
	@Test
	public void PR25() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón ver lista de ofertas disponibles
		elementos = PO_View.checkElement(driver, "id", "btn_viewOffers");
		elementos.get(0).click();

		PO_ViewOffers.purchaseOffer(driver, "GE3");
		elementos = PO_View.checkElement(driver, "id", "errorDinero");
		
		// Comprobamos el saldo
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'profile-menu')]/a");
		elementos.get(0).click();
		elementos = PO_View.checkElement(driver, "id", "saldo");
		assertEquals("100.0€", elementos.get(0).getText()); // 100€-100€
	}
	
	@Test
	public void PR26() {
	// Entramos como usuario
			PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
			PO_LoginView.fillForm(driver, "christian@email.com", "123456");

			// Seleccionamos la gestion del area personal
			List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
			elementos.get(0).click();

			// Pinchamos en el botón ver lista de ofertas propias
			elementos = PO_View.checkElement(driver, "id", "btn_purchased");
			elementos.get(0).click();

			// Obtenemos las ofertas que se ven en la vista
			List<Offer> ofertasVista = PO_OffersPurchased.ofertasCompradas(driver);

			
			List<Offer> ofertasBBDD = offerService.getOffersByPurchaser(usersService.getUserByEmail("christian@email.com"));

			assertEquals(ofertasBBDD.size(), ofertasVista.size());
	}
	
	/*
	 * Visualizar al menos cuatro páginas en Español/Inglés/Español (comprobando que algunas
de las etiquetas cambian al idioma correspondiente). Página principal/Opciones Principales de
Usuario/Listado de Usuarios de Admin/Vista de alta de Oferta.
	 */
	@Test
	public void PR27() {
		PO_Properties prop = new PO_Properties("messages");
		/* VISTA PRINCIPAL */
		// ES
		WebElement titulo = driver.findElement(By.id("title_home"));
		String tituloText = titulo.getText();

		WebElement descripcion = driver.findElement(By.id("description_home"));
		String descripcionText = descripcion.getText();

		assertEquals(prop.getString("index.title", prop.getSPANISH()), tituloText);
		assertEquals(prop.getString("index.description", prop.getSPANISH()), descripcionText);

		// EN
		PO_NavView.changeIdiom(driver, "btnEnglish");
		titulo = driver.findElement(By.id("title_home"));
		tituloText = titulo.getText();

		descripcion = driver.findElement(By.id("description_home"));
		descripcionText = descripcion.getText();

		assertEquals(prop.getString("index.title", prop.getENGLISH()), tituloText);
		assertEquals(prop.getString("index.description", prop.getENGLISH()), descripcionText);

		// ES
		PO_NavView.changeIdiom(driver, "btnSpanish");
		titulo = driver.findElement(By.id("title_home"));
		tituloText = titulo.getText();

		descripcion = driver.findElement(By.id("description_home"));
		descripcionText = descripcion.getText();

		assertEquals(prop.getString("index.title", prop.getSPANISH()), tituloText);
		assertEquals(prop.getString("index.description", prop.getSPANISH()), descripcionText);

		/* OPCIONES PRINCIPALES DE USUARIO ESTANDAR */
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// ES
		List<WebElement> elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		String menuOfertas_text = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		String menuPersonal_text = elementos.get(0).getText();

		assertEquals(prop.getString("nav.offer_management", prop.getSPANISH()), menuOfertas_text);
		assertEquals(prop.getString("nav.private_area", prop.getSPANISH()), menuPersonal_text);

		// EN
		PO_NavView.changeIdiom(driver, "btnEnglish");
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		menuOfertas_text = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		menuPersonal_text = elementos.get(0).getText();

		assertEquals(prop.getString("nav.offer_management", prop.getENGLISH()), menuOfertas_text);
		assertEquals(prop.getString("nav.private_area", prop.getENGLISH()), menuPersonal_text);

		// ES
		PO_NavView.changeIdiom(driver, "btnSpanish");
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		menuOfertas_text = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'personal-menu')]/a");
		menuPersonal_text = elementos.get(0).getText();

		assertEquals(prop.getString("nav.offer_management", prop.getSPANISH()), menuOfertas_text);
		assertEquals(prop.getString("nav.private_area", prop.getSPANISH()), menuPersonal_text);

		/* Listado Usuarios Admin */
		// Pinchamos en la opción del menú del perfil del usuario
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'profile-menu')]/a");
		elementos.get(0).click();
		// Pinchamos en el botón desconectar
		elementos = PO_View.checkElement(driver, "id", "btn_logout");
		elementos.get(0).click();

		// Entramos como admin
		PO_LoginView.fillForm(driver, "admin@email.com", "admin");

		// Entramos en la lista de usuarios
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'users-menu')]/a");
		elementos.get(0).click();
		// Pinchamos en el botón ver lista de usuarios
		elementos = PO_View.checkElement(driver, "id", "btn_userlist");
		elementos.get(0).click();

		// ES
		elementos = PO_View.checkElement(driver, "id", "title_listUsers");
		String titulo_lista = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "id", "title_explain_listUsers");
		String explicacion_lista = elementos.get(0).getText();

		assertEquals(prop.getString("users.view.title", prop.getSPANISH()), titulo_lista);
		assertEquals(prop.getString("users.view.explanation", prop.getSPANISH()), explicacion_lista);

		// EN
		PO_NavView.changeIdiom(driver, "btnEnglish");

		elementos = PO_View.checkElement(driver, "id", "title_listUsers");
		titulo_lista = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "id", "title_explain_listUsers");
		explicacion_lista = elementos.get(0).getText();

		assertEquals(prop.getString("users.view.title", prop.getENGLISH()), titulo_lista);
		assertEquals(prop.getString("users.view.explanation", prop.getENGLISH()), explicacion_lista);

		// ES
		PO_NavView.changeIdiom(driver, "btnSpanish");

		elementos = PO_View.checkElement(driver, "id", "title_listUsers");
		titulo_lista = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "id", "title_explain_listUsers");
		explicacion_lista = elementos.get(0).getText();

		assertEquals(prop.getString("users.view.title", prop.getSPANISH()), titulo_lista);
		assertEquals(prop.getString("users.view.explanation", prop.getSPANISH()), explicacion_lista);

		/* VISTA DE ALTA DE OFERTA */
		// Pinchamos en la opción del menú del perfil del usuario
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'profile-menu')]/a");
		elementos.get(0).click();
		// Pinchamos en el botón desconectar
		elementos = PO_View.checkElement(driver, "id", "btn_logout");
		elementos.get(0).click();

		// Entramos como usuario normal
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");

		// Seleccionamos la gestión de ofertas
		elementos = PO_View.checkElement(driver, "free", "//li[contains(@id,'offers-menu')]/a");
		elementos.get(0).click();

		// Pinchamos en el botón de añadir oferta
		elementos = PO_View.checkElement(driver, "id", "btn_addOffer");
		elementos.get(0).click();

		// ES
		elementos = PO_View.checkElement(driver, "id", "title_addOffer");
		String titulo_add = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "id", "lbl_titleOffer");
		String titulo_offer = elementos.get(0).getText();

		assertEquals(prop.getString("offer.add", prop.getSPANISH()), titulo_add);
		assertEquals(prop.getString("offer.add.title", prop.getSPANISH()), titulo_offer);

		// EN
		PO_NavView.changeIdiom(driver, "btnEnglish");

		elementos = PO_View.checkElement(driver, "id", "title_addOffer");
		titulo_add = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "id", "lbl_titleOffer");
		titulo_offer = elementos.get(0).getText();

		assertEquals(prop.getString("offer.add", prop.getENGLISH()), titulo_add);
		assertEquals(prop.getString("offer.add.title", prop.getENGLISH()), titulo_offer);

		// ES
		PO_NavView.changeIdiom(driver, "btnSpanish");

		elementos = PO_View.checkElement(driver, "id", "title_addOffer");
		titulo_add = elementos.get(0).getText();

		elementos = PO_View.checkElement(driver, "id", "lbl_titleOffer");
		titulo_offer = elementos.get(0).getText();

		assertEquals(prop.getString("offer.add", prop.getSPANISH()), titulo_add);
		assertEquals(prop.getString("offer.add.title", prop.getSPANISH()), titulo_offer);

	}

	/*
	 * Intentar acceder sin estar autenticado a la opción de listado de usuarios del
	 * administrador. Se deberá volver al formulario de login
	 */
	@Test
	public void PR28() {
		driver.navigate().to(URL + "/user/list");

		PO_LoginView.checkElement(driver, "id", "login_title");
	}

	/*
	 * Intentar acceder sin estar autenticado a la opción de listado de ofertas
	 * propias de un usuario estándar. Se deberá volver al formulario de login.
	 */
	@Test
	public void PR29() {
		driver.navigate().to(URL + "/offer/my");

		PO_LoginView.checkElement(driver, "id", "login_title");
	}

	/*
	 * Intentar acceder sin estar autenticado a la opción de listado de ofertas
	 * propias de un usuario estándar. Se deberá volver al formulario de login.
	 */
	@Test
	public void PR30() {
		// Entramos como usuario
		PO_HomeView.clickOption(driver, "login", 2, "class", "btn btn-primary");
		PO_LoginView.fillForm(driver, "christian@email.com", "123456");
		driver.navigate().to(URL + "/admin/list");

		// SeleniumUtils.textoPresentePagina(driver,"HTTP Status 500 – Internal Server
		// Error");
		SeleniumUtils.textoPresentePagina(driver, "HTTP Status 403 – Forbidden");
	}
}
