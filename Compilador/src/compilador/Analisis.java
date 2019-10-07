package compilador;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
public class Analisis
{
	int renglon=1;
	ArrayList<String> impresion; //para la salida
	ListaDoble<Token> tokens;
	final Token vacio=new Token("", 9,0);
	boolean bandera=true;
	ArrayList<TabladeSimbolos> tablasimbolos = new ArrayList<TabladeSimbolos>();
	
	public ArrayList<TabladeSimbolos> getTabla() {
		return tablasimbolos ;
	}
	public Analisis(String ruta) {//Recibe el nombre del archivo de texto
		analisaCodigo(ruta);
		if(bandera) {
			impresion.add("No hay errores lexicos");
			
			analisisSintactio(tokens.getInicio());
			AnalizadorSemantico(tokens.getInicio());
			Semantico2(tokens.getInicio());
		}
		if(impresion.get(impresion.size()-1).equals("No hay errores lexicos"))
			impresion.add("No hay errores sintacticos");
		
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
		else if(Arrays.asList("True","False").contains(token)||Pattern.matches("^\\d+$",token)||Pattern.matches("[0-9]+.[0-9]+",token)) 
			tipo = Token.CONSTANTE;
		else if(token.equals("class")) 
			tipo =Token.CLASE;
		else {
			//Cadenas validas
			Pattern pat = Pattern.compile("^[a-zA-Z]+$");//Expresiones Regulares
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
		
			
			switch (to.getTipo()) // un switch para validar la estructura
			{
			case Token.MODIFICADOR:
				int sig=nodo.siguiente.dato.getTipo();
				// aqui se valida que sea 'public int' o 'public class' 
				if(sig!=Token.TIPO_DATO && sig!=Token.CLASE)// si lo que sigue 
					impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un tipo de dato");
				break;
			case Token.IDENTIFICADOR:
				// lo que puede seguir despues de un idetificador
				
				if(!(Arrays.asList("{","=",";","==",")").contains(nodo.siguiente.dato.getValor()))) 
					impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un simbolo");
				else
					if(nodo.anterior.dato.getValor().equals("class")) // se encontro la declaracion de la clase
					{
						tablasimbolos.add( new TabladeSimbolos(to.getValor(), " ", "class"," ",to.getLinea()));
					}
				break;
			// Estos dos entran en el mismo caso
			case Token.TIPO_DATO:
			case Token.CLASE:
				
	
				
				// si lo anterior fue modificador
				if (nodo.anterior!=null) 
					if(nodo.anterior.dato.getTipo()==Token.MODIFICADOR) {
						if(nodo.siguiente.dato.getTipo()!=Token.IDENTIFICADOR) 
							impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un identificador");
					}else
						impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un modificador");
				break;
			case Token.SIMBOLO:
				
			
				
				
				// Verificar que el mismo numero de parentesis y llaves que abren sean lo mismo que los que cierran
				if(to.getValor().equals("}")) 
				{
					if(cuenta("{")!=cuenta("}"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un {");
				}else if(to.getValor().equals("{")) {
					if(cuenta("{")!=cuenta("}"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un }");
				}
			
				else if(to.getValor().equals("(")) {
					if(cuenta("(")!=cuenta(")"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un )");
				}else if(to.getValor().equals(")")) {
					if(cuenta("(")!=cuenta(")"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un (");
				}
				// verificar la asignacion
				else if(to.getValor().equals("=")){
					
					
						if(nodo.anterior.dato.getTipo()==Token.IDENTIFICADOR) {	
							if(nodo.siguiente.dato.getTipo()!=Token.CONSTANTE)
								impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba una constante");
									
						}
					} 
					
				
				else if (to.getValor().equals(";"))
				{
					boolean banderita=false;
					try
					{

					if (nodo.anterior.anterior.anterior.anterior.dato.getTipo()==Token.TIPO_DATO && nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR && nodo.anterior.anterior.dato.getTipo()==Token.SIMBOLO&&nodo.anterior.dato.getTipo()==Token.CONSTANTE)
						tablasimbolos.add(new TabladeSimbolos(nodo.anterior.anterior.anterior.dato.getValor(),nodo.anterior.dato.getValor(),nodo.anterior.anterior.anterior.anterior.dato.getValor(),"Local",to.getLinea()));
					else if (nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR&&nodo.anterior.anterior.dato.getTipo()==Token.SIMBOLO&&nodo.anterior.dato.getTipo()==Token.CONSTANTE)
					{
						
						for (int i = 0; i < tablasimbolos.size(); i++) {
							if(tablasimbolos.get(i).getNombre().contains(nodo.anterior.anterior.anterior.dato.getValor())){
								tablasimbolos.get(i).setValor(nodo.anterior.dato.getValor());
								banderita=true;
							}
						}
						
						if(!banderita){
							impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un Tipo de Dato");
						}
						
					}
					} catch (Exception e){
						System.out.println(e.getMessage());
					}


				}

				
				break;
			
			case Token.CONSTANTE:
				if(nodo.anterior.dato.getValor().equals("="))
					if(nodo.siguiente.dato.getTipo()!=Token.OPERADOR_ARITMETICO&&!nodo.siguiente.dato.getValor().equals(";"))
						impresion.add("Error sintactico en linea "+to.getLinea()+ " asignacion no valida");
				
		
				break;
			case Token.PALABRA_RESERVADA:
				// verificar esructura de if
				
	
				
				
				if(to.getValor().equals("if"))
				{
					if(!nodo.siguiente.dato.getValor().equals("(")) {
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
					if(!bandera)
						impresion.add("Error sintactico en linea "+to.getLinea()+ " else no valido");
				}
				break;
			case Token.OPERADOR_LOGICO:
				// verificar que sea  'numero' + 'operador' + 'numero' 
				if (to.getValor().equals("==")){
					if (nodo.anterior.anterior.anterior.dato.getTipo()!=Token.PALABRA_RESERVADA){
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba una palabra reservada (if)");
					}
					
					if (nodo.anterior.anterior.dato.getTipo()!=Token.SIMBOLO){
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba un simbolo");
					}
					if (!nodo.siguiente.siguiente.dato.getValor().contains(")")){
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba un simbolo");
					}
				}
				if(nodo.anterior.dato.getTipo()!=Token.CONSTANTE && nodo.anterior.dato.getTipo()!=Token.IDENTIFICADOR  ) 
					impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba una Constante/Identificador");
				if(nodo.siguiente.dato.getTipo()!=Token.CONSTANTE && nodo.siguiente.dato.getTipo()!=Token.IDENTIFICADOR )
					impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba una Constante/Identificador");
				break;
			}

			
			
				
			
			
			analisisSintactio(nodo.siguiente);
			return to;
		}
		return  vacio;// para no regresar null y evitar null pointer
	}
	
	
	public  Token AnalizadorSemantico (NodoDoble<Token> nodo){
		
		
		Token  to;
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;
		
			
			
		
			
			String aux;
			String aux2,auxiliarTipo;
			int aux3,renglon;

			
			for (int i = 0; i < tablasimbolos.size(); i++) {
				
				aux = tablasimbolos.get(i).tipo;
				renglon = tablasimbolos.get(i).getRenglon();
				
				if(aux.contains("int")){
					aux2=tablasimbolos.get(i).getValor();
					auxiliarTipo =TipoCadena(aux2);
					
					if (EsNumeroEntero(aux2) == false) {
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un INT");
	
			        } 
				}
				else if(aux.contains("float")){
					
					aux2=tablasimbolos.get(i).getValor();
					auxiliarTipo =TipoCadena(aux2);
					
					if (Esfloat(aux2) == false) {
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un FLOAT");
	
			        } 
					
					
				}
				else if(aux.contains("char")){
					
					aux2=tablasimbolos.get(i).getValor();
					auxiliarTipo =TipoCadena(aux2);
					
					if (EsChar(aux2) == false) {
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un CHAR");
	
			        } 
					
					
					
					
				}else if(aux.contains("boolean")){
					
					aux2=tablasimbolos.get(i).getValor();
					auxiliarTipo =TipoCadena(aux2);
					
					if (EsBoolean(aux2) == false) {
						impresion.add("Error Semantico en la linea "+renglon+ ", se recibió un "+auxiliarTipo+ " y se esperaba un BOOLEAN");
	
			        } 
					
					
				
				
				
				
			//Punto 3
			
					
				
				
				
				
			}
			
			
			
		}
		
			
		
		
		
			
		}
		
		
		
		

		return vacio;
		
	}
	
	
	public Token Semantico2(NodoDoble<Token> nodo) {
		Token  to;
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;


			if(to.getTipo()==Token.IDENTIFICADOR){
			String auxiliar = to.getValor();
			boolean bandera2 = false;
			
			for (int i = 0; i < tablasimbolos.size(); i++) {
				
				if(tablasimbolos.get(i).getNombre().contains(auxiliar)){
					bandera2=true;
				}
				
			}
			
			if(!bandera2)
				impresion.add("Error semantico en linea "+to.getLinea()+ " se uso la variable "+auxiliar+" no está declarada");


		}
		
		Semantico2(nodo.siguiente);
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

       if(cadena.length()==1)
    	   return true;
    			   return false;

    }
	
	
	public static boolean EsBoolean(String cadena) {


      if(cadena.contains("True")||cadena.contains("False"))
    	  return true;
      		return false;

    }
	
	
	public static String TipoCadena(String cadena) {

        String resultado= "hola";

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
        	resultado = "INT";
        	return resultado;
        }
        
        if(Pattern.matches("[0-9]+.[0-9]+",cadena)){
        	resultado = "FLOAT";
        }
        
        
        if(Pattern.matches("[a-zA-Z]",cadena)){
        	resultado = "CHAR";
        }
        
        if(cadena.contains("True")||cadena.contains("False")){
        	resultado = "BOOLEAN";
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
}
