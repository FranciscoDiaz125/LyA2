package compilador;
import java.awt.Color;
import java.awt.image.BandedSampleModel;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.plaf.synth.SynthSpinnerUI;
public class Analisis
{
	int renglon=1;
	ArrayList<String> impresion; //para la salida
	ListaDoble<Token> tokens;
	final Token vacio=new Token("", 9,0);
	boolean bandera=true,banderaclase=false, banderaErroresSemanticos = false,banderaErroresSintacticos = false;
	public ColorCeldas color = new ColorCeldas(4);
	ArrayList<TabladeSimbolos> tablasimbolos = new ArrayList<TabladeSimbolos>();
	ArrayList<Arbolito> arbol = new ArrayList<Arbolito>();
	ArrayList<String> expresion = new ArrayList<String>();


	String Anterior1Valor;
	String Anterior2Valor;
	String Anterior3Valor;
	String Anterior4Valor;
	String Anterior5Valor;
	String cadenaauxiliar ="";
	int Anterior1Tipo ;
	int Anterior2Tipo;
	int Anterior3Tipo;
	int Anterior4Tipo;
	int Anterior5Tipo;
	String Siguiente1Valor;
	String Siguiente2Valor;
	int Siguiente1Tipo;	
	
	
	public ArrayList<TabladeSimbolos> getTabla() {
		return tablasimbolos ;
	}
	
	public ArrayList<Arbolito> getTabla2() {
		return arbol ;
	}
	
	public Analisis(String ruta) {//Recibe el nombre del archivo de texto
		analisaCodigo(ruta);
		if(bandera) {
			impresion.add("No hay errores lexicos");
			
			banderaErroresSemanticos = false;
			banderaErroresSintacticos = false;
			analisisSintactio(tokens.getInicio());
			AnalizadorSemantico(tokens.getInicio());
			Semantico2(tokens.getInicio());
			VerificarClase(tokens.getInicio());
			VerificarClase(tokens.getInicio());
			
			if(!banderaclase){
				impresion.add("Falta la inicialización de la clase!");
			}
			
		}
		

		if(!banderaErroresSintacticos)
			impresion.add("No hay errores sintacticos!");
		
		if(!banderaErroresSemanticos)
			impresion.add("No hay errores semanticos!");

		
		for (int i = 0; i < tablasimbolos.size(); i++) {
			System.out.println(tablasimbolos.get(i).toString());
		}
		System.out.println();
			
	}
	public void analisaCodigo(String ruta) {
		String linea="", token="";
		StringTokenizer tokenizer;
		try{
	          FileReader file = new FileReader(ruta);
	          BufferedReader archivoEntrada = new BufferedReader(file);
	          linea = archivoEntrada.readLine();
	          impresion=new ArrayList<String>();
	          tokens = new ListaDoble<Token>();
	          while (linea != null){
	        	    linea = separaDelimitadores(linea);
	                tokenizer = new StringTokenizer(linea);
	                while(tokenizer.hasMoreTokens()) {
	                	token = tokenizer.nextToken();
	                	analisisLexico(token);
	                }
	                linea=archivoEntrada.readLine();
	                renglon++;
	          }
	          archivoEntrada.close();
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null,"No se encontro el archivo favor de checar la ruta","Alerta",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	
	public void analisisLexico(String token) {
		
		AppCompilador.eliminarErrorSintactico();
		AppCompilador.eliminarErrorSemantico();
		int tipo=0;
		//Se usan listas con los tipos de token
		// Esto se asemeja a un in en base de datos 
		//Ejemplo select * from Clientes where Edad in (18,17,21,44)
		if(Arrays.asList("public","static","private","protected").contains(token)) 
			tipo = Token.MODIFICADOR;
		else if(Arrays.asList("if","else").contains(token)) 
			tipo = Token.PALABRA_RESERVADA;
		else if(Arrays.asList("int","char","float","boolean").contains(token))
			tipo = Token.TIPO_DATO;
		else if(Arrays.asList("(",")","{","}","=",";").contains(token))
			tipo = Token.SIMBOLO;
		else if(Arrays.asList("<","<=",">",">=","==","!=").contains(token))
			tipo = Token.OPERADOR_LOGICO; 
		else if(Arrays.asList("+","-","*","/").contains(token))
			tipo = Token.OPERADOR_ARITMETICO;
		else if(Arrays.asList("True","False").contains(token)||Pattern.matches("^[0-9]+$",token)
				||Pattern.matches("[0-9]+.[0-9]+",token)||Pattern.matches("'[a-zA-Z]'",token)) 
			tipo = Token.CONSTANTE;
		else if(token.equals("class")) 
			tipo =Token.CLASE;
		else {
			//Cadenas validas
			Pattern pat = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$")  ;//Expresiones Regulares
			Matcher mat = pat.matcher(token);
				
			if(mat.find()) 
				tipo = Token.IDENTIFICADOR;
			
	
			else {
				impresion.add("Error lexico en la linea "+renglon+" token "+token);
				bandera = false;
				return;
			}
		}
		tokens.insertar(new Token(token,tipo,renglon));
		impresion.add(new Token(token,tipo,renglon).toString());
		

		
	}
	
	
	
	
	
	



	public Token analisisSintactio(NodoDoble<Token> nodo) {

		Token  to;


		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;

			
			try{

				Anterior1Valor = nodo.anterior.dato.getValor();
				Anterior1Tipo = nodo.anterior.dato.getTipo();
				Anterior2Valor = nodo.anterior.anterior.dato.getValor();
				Anterior2Tipo = nodo.anterior.anterior.dato.getTipo();
				Anterior3Valor = nodo.anterior.anterior.anterior.dato.getValor();
				Anterior3Tipo = nodo.anterior.anterior.anterior.dato.getTipo();
				Anterior4Valor = nodo.anterior.anterior.anterior.anterior.dato.getValor();
				Anterior4Tipo = nodo.anterior.anterior.anterior.anterior.dato.getTipo();
				Anterior5Valor = nodo.anterior.anterior.anterior.anterior.anterior.dato.getValor();
				Anterior5Tipo= nodo.anterior.anterior.anterior.anterior.anterior.dato.getTipo();
				
				
			}catch (Exception e){
				e.getMessage();
			}
			
			try{
				Siguiente1Valor = nodo.siguiente.dato.getValor();
				Siguiente1Tipo = nodo.siguiente.dato.getTipo();
				Siguiente2Valor = nodo.siguiente.siguiente.dato.getValor();
				
				
			}catch (Exception e){
				e.getMessage();
			}
			

			try{
				switch (to.getTipo()) // un switch para validar la estructura
				{
				case Token.MODIFICADOR:
					int sig=Siguiente1Tipo;
					// aqui se valida que sea 'public int' o 'public class' 
					if(sig!=Token.TIPO_DATO && sig!=Token.CLASE)// si lo que sigue 
					{
						AppCompilador.enviarErrorSintactico(to.getLinea());
						banderaErroresSintacticos = true;
						impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un tipo de dato");
					}
					break;
				case Token.IDENTIFICADOR:
					// lo que puede seguir despues de un idetificador
					try{
						if((Arrays.asList("{","=",";","==",")").contains(Siguiente1Valor))) {
							if(Anterior1Valor.equals("class")) // se encontro la declaracion de la clase
							{
								tablasimbolos.add( new TabladeSimbolos(to.getValor(), " ", "class"," ",to.getLinea()));

							}
						}

					}catch (Exception e){
						AppCompilador.enviarErrorSintactico(to.getLinea());
						banderaErroresSintacticos = true;
						impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un simbolo");
						System.out.println(e.getMessage());
					}

					break;
					// Estos dos entran en el mismo caso
				case Token.TIPO_DATO:
				case Token.CLASE:

					// si lo anterior fue modificador
					if (nodo.anterior!=null) 
						if(Anterior1Tipo==Token.MODIFICADOR) {
							if(Siguiente1Tipo!=Token.IDENTIFICADOR) {
								AppCompilador.enviarErrorSintactico(to.getLinea());
								banderaErroresSintacticos = true;
								impresion.add("Error sintactico en la linea "+to.getLinea()+" "
										+ "se esperaba un identificador");
							}

						}else{
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un modificador");

						}
					break;
				case Token.SIMBOLO:




					// Verificar que el mismo numero de parentesis y llaves que abren sean lo mismo que los que cierran
					if(to.getValor().equals("}")) 
					{
						if(cuenta("{")!=cuenta("}")){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un {");
						}
					}else if(to.getValor().equals("{")) {
						if(cuenta("{")!=cuenta("}")){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un }");
						}
					}

					else if(to.getValor().equals("(")) {
						if(cuenta("(")!=cuenta(")")){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un )");
						}
					}else if(to.getValor().equals(")")) {
						if(cuenta("(")!=cuenta(")")){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un (");
						}
					}
					// verificar la asignacion
					else if(to.getValor().equals("=")){


						if(Anterior1Tipo==Token.IDENTIFICADOR) {	
							if(Siguiente1Tipo!=Token.CONSTANTE && !Siguiente1Valor.contains("(") && Siguiente1Tipo!=Token.IDENTIFICADOR){
								AppCompilador.enviarErrorSintactico(to.getLinea());
								banderaErroresSintacticos = true;
								impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba una constante");
								System.out.println("aaaaaaaaaaa");

							}

						}
					} 


					else if (to.getValor().equals(";"))
					{


						int aux=0;


						boolean banderita=false;
						try
						{

							if (Anterior2Tipo==Token.TIPO_DATO && Anterior1Tipo==Token.IDENTIFICADOR){
								tablasimbolos.add(new TabladeSimbolos(Anterior1Valor,"",Anterior2Valor,"Global",to.getLinea()));

							}
							
							/////////////////////////////////////////////////////////
							/////////////////////////////////////////////////////////
							/////////////////////////////////////////////////////////
							//PUNTO 4: VALIDAR LAS VARIABLES YA DECLARADAS
							/////////////////////////////////////////////////////////
							/////////////////////////////////////////////////////////
							/////////////////////////////////////////////////////////
							else if (Anterior4Tipo==Token.TIPO_DATO 
									&& Anterior3Tipo==Token.IDENTIFICADOR 
									&& Anterior2Tipo==Token.SIMBOLO
									&&Anterior1Tipo==Token.CONSTANTE){

								int x =0,auxRenglon=0;
								boolean bandera=false;
								for (int i = 0; i < tablasimbolos.size(); i++) {
									if (tablasimbolos.get(i).getNombre().equals(Anterior3Valor) ){
										x++;
										auxRenglon=i;
									}

								}
								if(Anterior4Tipo==Token.TIPO_DATO && x>0 && Anterior3Tipo==Token.IDENTIFICADOR){
									AppCompilador.enviarErrorSemantico(to.getLinea());
									banderaErroresSemanticos=true;
									impresion.add("Error semantico en linea "+to.getLinea()+ " la variable "+Anterior3Valor+" ya habia sido declarada en la linea "+tablasimbolos.get(auxRenglon).renglon);
									bandera=true;
								}

								if(!bandera)
									tablasimbolos.add(new TabladeSimbolos(Anterior3Valor,Anterior1Valor,Anterior4Valor,"Global",to.getLinea()));

							}
							else if ( (Anterior4Tipo==Token.CONSTANTE 
									&& Anterior3Tipo==Token.OPERADOR_ARITMETICO 
									&& Anterior2Tipo==Token.CONSTANTE
									&& Anterior1Valor.contains(")"))  
									|| 
									(Anterior4Tipo==Token.CONSTANTE
									&& Anterior3Tipo==Token.SIMBOLO 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.CONSTANTE) 
									|| 
									( Anterior3Tipo==Token.CONSTANTE 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.CONSTANTE)
									|| 
									( Anterior3Tipo==Token.IDENTIFICADOR 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.IDENTIFICADOR)
									|| 
									( Anterior3Tipo==Token.IDENTIFICADOR
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.CONSTANTE)
									|| 
									( Anterior3Tipo==Token.CONSTANTE 
									&& Anterior2Tipo==Token.OPERADOR_ARITMETICO
									&& Anterior1Tipo==Token.IDENTIFICADOR)){
								
								
								
								NodoDoble<Token> nodoaux = nodo;
								NodoDoble<Token> nodoaux2 = nodo;
								NodoDoble<Token> nodoaux3 = nodo;
								while(nodoaux!=null){
									String aux2 = nodoaux.anterior.dato.getValor();
									System.out.println(aux2);
									if(aux2.contains("="))
										break;
									
									nodoaux = nodoaux.anterior;
								}
						
								
								while(nodoaux!=null){
									String aux2 = nodoaux.dato.getValor();
									System.out.println(aux2);
									if(aux2.contains(";"))
										break;
									
									expresion.add(aux2);
									nodoaux = nodoaux.siguiente;
								}
								

								
								for (int i = 0; i < expresion.size(); i++) {
									for (int j = 0; j < tablasimbolos.size(); j++) {
										
										if(tablasimbolos.get(j).getNombre().equals(expresion.get(i))){
											System.out.println(tablasimbolos.get(j).getNombre());
											System.out.println(expresion.get(i));
											expresion.set(i, tablasimbolos.get(j).getValor());
									}
										
									}
									
								}
								
								ArrayList<String> expresion2 = new ArrayList<String>(expresion);

										for (int i = 0; i < expresion.size(); i++) {
											
											if(expresion.get(i).contains("("))
											expresion.set(i, "ParAbierto");
											
											else if(expresion.get(i).contains(")"))
												expresion.set(i, "ParCerrado");
											
											else if(expresion.get(i).contains("/"))
												expresion.set(i, "Div");
											
											else if(expresion.get(i).contains("*"))
												expresion.set(i, "Multi");
											
											else if(expresion.get(i).contains("+"))
												expresion.set(i, "Suma");
											
											else if(expresion.get(i).contains("-"))
												expresion.set(i, "Resta");
											
										}
										int Resultadofinal=0;
										int contador =1;
										
										for (int i = 0; i < expresion.size(); i++) {
											
												
												if(expresion.get(i).contains("ParAbierto") ){
													
													if (expresion.get(i).contains("ParAbierto")){
														
														int aux5 = i;
														int aux6 = 0 ;
														boolean banderaParentesis = false;
														
														for (int j = 0; j < expresion.size(); j++) {
															if(expresion.get(j).contains("ParCerrado")){
															aux6 = j;
															break;
															}
														}
														
														
														while(!banderaParentesis){
															
														//(3 * 6 - 2)	
															
															for (int j = aux5; j < aux6; j++) {

																if(expresion.get(j).contains("Div")){
																	Resultadofinal =  dividir(expresion.get(j-1), expresion.get(j+1));
																	expresion2.set(j,"temp"+contador);
																	arbol.add(new Arbolito("/",expresion2.get(j-1),expresion2.get(j+1),expresion2.get(j)));
																	
																	expresion2.remove(j+1);
																	expresion2.remove(j-1);
																	
																	expresion.set(j,Resultadofinal+"" );
																	expresion.remove(j+1);
																	expresion.remove(j-1);
																	
																	aux6 = aux6 - 2;
																	contador++;
																}
																 if (expresion.get(j).contains("Multi")){
																	Resultadofinal =  multiplicar(expresion.get(j-1), expresion.get(j+1));
																	expresion2.set(j,"temp"+contador);
																	arbol.add(new Arbolito("*",expresion2.get(j-1),expresion2.get(j+1),expresion2.get(j)));
																	expresion2.remove(j+1);
																	expresion2.remove(j-1);
																	
																	expresion.set(j,Resultadofinal+"" );
																	expresion.remove(j+1);
																	expresion.remove(j-1);
																	aux6 = aux6 - 2;

																	contador++;
																}
															}
															
															 if (expresion.get(i+2).contains("Suma")){
																Resultadofinal =  Sumar(expresion.get(i+1), expresion.get(i+3));
																expresion2.set(i+2,"temp"+contador);
																arbol.add(new Arbolito("+",expresion2.get(i+1),expresion2.get(i+3),expresion2.get(i+2)));
																expresion2.remove(i+3);
																expresion2.remove(i+1);
																
																expresion.set(i+1,Resultadofinal+"" );
																expresion.remove(i+2);
																expresion.remove(i+2);
																contador++;
															}
															 if (expresion.get(i+2).contains("Resta")){
																Resultadofinal =  Restar(expresion.get(i+1), expresion.get(i+3));
																expresion2.set(i+2,"temp"+contador);
																arbol.add(new Arbolito("-",expresion2.get(i+1),expresion2.get(i+3),expresion2.get(i+2)));
																expresion2.remove(i+3);
																expresion2.remove(i+1);
																
																expresion.set(i+1,Resultadofinal+"" );
																expresion.remove(i+2);
																expresion.remove(i+2);
											
																contador++;
															}
															


															if(expresion.get(i+2).contains("ParCerrado"))	{
																expresion.remove(i+2);
																expresion.remove(i);
																expresion2.remove(i+2);
																expresion2.remove(i);
																banderaParentesis = true;
															}
															

														
															
															
														}
													
													}
												}
										}
										
											
											for (int i = 0; i < expresion.size(); i++) {

											if(expresion.get(i).contains("Multi") || expresion.get(i).contains("Div")){
												
												
												if (expresion.get(i).contains("Multi")){
													Resultadofinal =  multiplicar(expresion.get(i-1), expresion.get(i+1));
													
													expresion2.set(i,"temp"+contador);
												
		
													arbol.add(new Arbolito("*",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
													expresion2.remove(i+1);
													expresion2.remove(i-1);
													
													expresion.set(i-1,Resultadofinal+"" );
													expresion.remove(i);
													expresion.remove(i);
													
													
													i--;
													contador++;
												}
												else if (expresion.get(i).contains("Div")){
													Resultadofinal =  dividir(expresion.get(i-1), expresion.get(i+1));
													
													expresion2.set(i,"temp"+contador);
												
		
													arbol.add(new Arbolito("/",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
													expresion2.remove(i+1);
													expresion2.remove(i-1);
													
													expresion.set(i-1,Resultadofinal+"" );
													expresion.remove(i);
													expresion.remove(i);
													
													
													i--;
													contador++;
												}
											
											}
											
											
										}
				
										
										for (int i = 0; i < expresion.size(); i++) {
											
											if(expresion.get(i).contains("Suma") || expresion.get(i).contains("Resta")){
												
												if (expresion.get(i).contains("Suma")){

													Resultadofinal =  Sumar(expresion.get(i-1), expresion.get(i+1));
												
													expresion2.set(i,"temp"+contador);
													
													
													arbol.add(new Arbolito("+",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
													expresion2.remove(i+1);
													expresion2.remove(i-1);
													
													expresion.set(i-1,Resultadofinal+"" );
													expresion.remove(i);
													expresion.remove(i);
													i--;
													contador++;
												}
												
												else if (expresion.get(i).contains("Resta")){
													if(expresion.get(i).contains("Resta")){
														Resultadofinal =  Restar(expresion.get(i-1), expresion.get(i+1));
											
														expresion2.set(i,"temp"+contador);
														
														
														arbol.add(new Arbolito("-",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
														expresion2.remove(i+1);
														expresion2.remove(i-1);
														
														expresion.set(i-1,Resultadofinal+"" );
														expresion.remove(i);
														expresion.remove(i);
														i--;
														contador++;
													}
													
												}
												
											}

												
											
										}
							

				

										int Tipo, nombre;
										String auxTipo ="", auxNombre = "";
										while(nodoaux2!=null){
											Tipo = nodoaux2.anterior.dato.getTipo();
											System.out.println(Tipo);
											if(Tipo==2 ){
												auxTipo = nodoaux2.anterior.dato.getValor();
												auxNombre = nodoaux2.dato.getValor();
												break;

											}
											
											nodoaux2 = nodoaux2.anterior;
										}
									
										/*while(nodoaux3!=null){
											nombre = nodoaux3.anterior.dato.getTipo();
											System.out.println(nombre);
											if(nombre==7){
												auxNombre = nodoaux3.anterior.dato.getValor();
												break;
											}
											
											nodoaux3 = nodoaux3.anterior;
										}*/
										
									arbol.add(new Arbolito("=",expresion2.get(0)," ",auxNombre));
									tablasimbolos.add(new TabladeSimbolos(auxNombre,Resultadofinal+"",auxTipo,"Global",to.getLinea()));

									
									expresion.remove(0);
									expresion2.remove(0);

							
								

						}


							else if (Anterior3Tipo==Token.IDENTIFICADOR
									&&Anterior2Tipo==Token.SIMBOLO
									&&Anterior1Tipo==Token.CONSTANTE)
							{



								for (int i = 0; i < tablasimbolos.size(); i++) {
									if(tablasimbolos.get(i).getNombre().contains(Anterior3Valor)){
										tablasimbolos.get(i).setValor(Anterior1Valor);
										banderita=true;
									}
								}

								if(!banderita){
									AppCompilador.enviarErrorSintactico(to.getLinea());
									banderaErroresSintacticos = true;
									impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un Tipo de Dato");
								}

							}

							else if (Anterior3Tipo==Token.IDENTIFICADOR
									&&Anterior2Tipo==Token.SIMBOLO
									&&Anterior1Tipo==Token.CONSTANTE)
							{



								for (int i = 0; i < tablasimbolos.size(); i++) {
									if(tablasimbolos.get(i).getNombre().contains(Anterior3Valor)){
										tablasimbolos.get(i).setValor(Anterior1Valor);
										banderita=true;
									}
								}

								if(!banderita){
									AppCompilador.enviarErrorSintactico(to.getLinea());
									banderaErroresSintacticos = true;
									impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un Tipo de Dato");
								}

							}



						} catch (Exception e){
							System.out.println(e.getMessage());
						}



					}


					break;

				case Token.CONSTANTE:
					if(Anterior1Valor.equals("="))
						if(Siguiente1Tipo!=Token.OPERADOR_ARITMETICO
						&&!Siguiente1Valor.equals(";")){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en linea "+to.getLinea()+ " asignacion no valida");
						}


					break;
				case Token.PALABRA_RESERVADA:
					// verificar esructura de if




					if(to.getValor().equals("if"))
					{
						if(!Siguiente1Valor.equals("(")) {
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un (");
						}


					}
					else 
					{
						// si es un else, buscar en los anteriores y si no hay un if ocurrira un error
						NodoDoble<Token> aux = nodo.anterior;
						boolean bandera=false;
						while(aux!=null&&!bandera) {
							if(aux.dato.getValor().equals("if"))
								bandera=true;
							aux =aux.anterior;
						}
						if(!bandera){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en linea "+to.getLinea()+ " else no valido");

						}
					}
					break;
				case Token.OPERADOR_LOGICO:
					// verificar que sea  'numero' + '==' + 'numero' 
					if (to.getValor().equals("==")){
						if (Anterior3Tipo!=Token.PALABRA_RESERVADA){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba una palabra reservada (if)");
						}

						if (Anterior2Tipo!=Token.SIMBOLO){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba un simbolo");
						}
						if (!Siguiente2Valor.contains(")")){
							AppCompilador.enviarErrorSintactico(to.getLinea());
							banderaErroresSintacticos = true;
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba un simbolo");

						}
					}
					if(Anterior1Tipo!=Token.CONSTANTE && Anterior1Tipo!=Token.IDENTIFICADOR  ) {
						AppCompilador.enviarErrorSintactico(to.getLinea());
						banderaErroresSintacticos = true;
						impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba una Constante/Identificador");

					}
					if(Siguiente1Tipo!=Token.CONSTANTE && Siguiente1Tipo!=Token.IDENTIFICADOR ){
						AppCompilador.enviarErrorSintactico(to.getLinea());
						banderaErroresSintacticos = true;
						impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba una Constante/Identificador");

					}
					
					
					/////////////////////////////////////////////////////////
					/////////////////////////////////////////////////////////
					/////////////////////////////////////////////////////////
					//PUNTO 5: VALIDAR OPERANDOS DE TIPOS COMPATIBLES
					/////////////////////////////////////////////////////////
					/////////////////////////////////////////////////////////
					/////////////////////////////////////////////////////////

					String operando1,operando2;

					if (Anterior1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Anterior1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando1= TipoCadena(valor);

					}else
					operando1= TipoCadena(Anterior1Valor);
					
					if (Siguiente1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Siguiente1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando2= TipoCadena(valor);

					}else
					operando2= TipoCadena(Siguiente1Valor);
					

					if(!operando1.contains(operando2)){
						AppCompilador.enviarErrorSemantico(to.getLinea());
						banderaErroresSemanticos=true;
						impresion.add("Error semantico en linea "+to.getLinea()+ ", no coinciden los tipos de los operandos ("+operando1+"/"+operando2+")");
						System.out.println("33");
					}



					break;

				case Token.OPERADOR_ARITMETICO:

					String operando3,operando4;

					if (Anterior1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Anterior1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando3= TipoCadena(valor);

					}else
					operando3= TipoCadena(Anterior1Valor);
					if(operando3.equals(""))
						operando3= "int";
					
					if (Siguiente1Tipo==Token.IDENTIFICADOR)
					{
						String valor="";
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if (tablasimbolos.get(i).getNombre().equals(Siguiente1Valor))
								valor = tablasimbolos.get(i).getValor();
						}
						operando4= TipoCadena(valor);

					}else
					operando4= TipoCadena(Siguiente1Valor);
					

					if(!operando3.contains(operando4)){
						AppCompilador.enviarErrorSemantico(to.getLinea());
						banderaErroresSemanticos=true;
						impresion.add("Error semantico en linea "+to.getLinea()+ ", no coinciden los tipos de los operandos ("+operando3+"/"+operando4+")");
						System.out.println("99");
					}



					break;

				}



			}catch (Exception e)
			{
				System.out.println(e.getMessage());
			}



			analisisSintactio(nodo.siguiente);
			return to;
		}
		return  vacio;// para no regresar null y evitar null pointer
	}


	


	public  Token AnalizadorSemantico (NodoDoble<Token> nodo){

		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		//PUNTO 2: VALIDAR LA ASIGNACIÓN A UNA VARIABLE
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		
		Token  to;
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			String aux;
			String aux2,auxiliarTipo = "";
			int aux3,renglon;


			for (int i = 0; i < tablasimbolos.size(); i++) {

				aux = tablasimbolos.get(i).tipo;
				renglon = tablasimbolos.get(i).getRenglon();

				if(aux.contains("int")){
					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (EsNumeroEntero(aux2) == false && !aux2.isEmpty()) {
						AppCompilador.enviarErrorSemantico(renglon);
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un INT");


					} 
				}
				else if(aux.contains("float")){

					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (Esfloat(aux2) == false && !aux2.isEmpty()) {
						AppCompilador.enviarErrorSemantico(renglon);
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un FLOAT");

					} 


				}
				else if(aux.contains("char")){

					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (EsChar(aux2) == false && !aux2.isEmpty()) {
						AppCompilador.enviarErrorSemantico(renglon);
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un CHAR");

					} 




				}else if(aux.contains("boolean")){

					aux2=tablasimbolos.get(i).getValor();
					if(!aux2.isEmpty())
						auxiliarTipo =TipoCadena(aux2);

					if (EsBoolean(aux2) == false && !aux2.isEmpty() ) {
						AppCompilador.enviarErrorSemantico(renglon);
						banderaErroresSemanticos=true;
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un BOOLEAN");

					} 

				}



			}






		}
		
		
		
		

		return vacio;
		
	}
	

	public Token Semantico2(NodoDoble<Token> nodo) {
		Token  to;
		
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		//PUNTO 3: VALIDAR LAS VARIABLES USADAS Y NO DECLARADAS
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////
		
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			if(to.getTipo()==Token.IDENTIFICADOR){
				String auxiliar = to.getValor();
				boolean bandera2 = false;

				for (int i = 0; i < tablasimbolos.size(); i++) {

					if(tablasimbolos.get(i).getNombre().equals(auxiliar)){
						bandera2=true;
					}
				}

				if(!bandera2){
					AppCompilador.enviarErrorSemantico(to.getLinea());
					banderaErroresSemanticos=true;
					impresion.add("Error semantico en linea "+to.getLinea()+ " se uso la variable "+auxiliar+" no está declarada");

				}


			}

			Semantico2(nodo.siguiente);
			return to;
		}
		return vacio;
	}


	public Token VerificarClase(NodoDoble<Token> nodo) {
		Token  to;


		if(banderaclase){
			return vacio;
		}

		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			if(to.getTipo()==Token.CLASE){

				banderaclase= true;

			}

			VerificarClase(nodo.siguiente);
			return to;
		}
		return vacio;
	}

	public static boolean EsNumeroEntero(String cadena) {

		boolean resultado;

		try {
			Integer.parseInt(cadena);
			resultado = true;
		} catch (NumberFormatException excepcion) {
			resultado = false;
		}

		return resultado;
	}

	public static boolean Esfloat(String cadena) {

		boolean resultado;

		try {
			Float.parseFloat(cadena);
			resultado = true;
		} catch (NumberFormatException excepcion) {
			resultado = false;
		}

		return resultado;
	}


	public static boolean EsChar(String cadena) {

		boolean resultado;

		if(Pattern.matches("'[a-zA-Z]'",cadena))
			return true;
		return false;

	}


	public static boolean EsBoolean(String cadena) {


		if(cadena.contains("True")||cadena.contains("False"))
			return true;
		return false;

	}


	public static String TipoCadena(String cadena) {

		String resultado= "";

		/*try {
            Integer.parseInt(cadena);
            resultado = "INT";
            return resultado;
        } catch (NumberFormatException excepcion) {
        }

        try {
        	Float.parseFloat(cadena);
            resultado = "FLOAT";
            return resultado;
        } catch (NumberFormatException excepcion) {
        }


        try {
        	Float.parseFloat(cadena);
            resultado = "FLOAT";
            return resultado;
        } catch (NumberFormatException excepcion) {
        }
		 */

		if(Pattern.matches("[0-9]+",cadena)){
			resultado = "int";
			return resultado;
		}

		if(Pattern.matches("[0-9]+.[0-9]+",cadena)){
			resultado = "float";
		}


		if(Pattern.matches("'[a-zA-Z]'",cadena)){
			resultado = "char";
		}

		if(cadena.contains("True")||cadena.contains("False")){
			resultado = "boolean";
		}

		return resultado;
	}



	// por si alguien escribe todo pegado 
	public String separaDelimitadores(String linea){
		for (String string : Arrays.asList("(",")","{","}","=",";")) {
			if(string.equals("=")) {
				if(linea.indexOf(">=")>=0) {
					linea = linea.replace(">=", " >= ");
					break;
				}
				if(linea.indexOf("<=")>=0) {
					linea = linea.replace("<=", " <= ");
					break;
				}
				if(linea.indexOf("==")>=0)
				{
					linea = linea.replace("==", " == ");
					break;
				}
			}
			if(linea.contains(string)) 
				linea = linea.replace(string, " "+string+" ");
		}
		return linea;
	}
	public int cuenta (String token) {

		int conta=0;
		NodoDoble<Token> Aux=tokens.getInicio();
		while(Aux !=null){
			if(Aux.dato.getValor().equals(token))
				conta++;
			Aux=Aux.siguiente;
		}	
		return conta;
	}
	public ArrayList<String> getmistokens() {
		return impresion;
	}



	public int Sumar (String uno, String dos){

		int suma =0;

		suma = suma+Integer.parseInt(uno)+Integer.parseInt(dos);


		return suma;
	}
	
	public int Restar (String uno, String dos){

		int Resta =0;

		Resta = Resta+Integer.parseInt(uno)-Integer.parseInt(dos);


		return Resta;
	}
	
	public int multiplicar (String uno, String dos){

		int multi =0;

		multi = multi+Integer.parseInt(uno)*Integer.parseInt(dos);


		return multi;
	}
	
	public int dividir (String uno, String dos){

		int div =0;

		div = div+ (int)( Integer.parseInt(uno)/Integer.parseInt(dos));


		return div;
	}
	
	
	public String jerarquias (int i, String aux){
		
		
		return aux;
	}
	
}
