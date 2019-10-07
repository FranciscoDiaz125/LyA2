package compilador;


public class Identificador {

	public String toString() {
		return "Identificador [nombre=" + nombre + ", valor=" + valor + ", tipo=" + tipo + "]";
	}
	String nombre;
	String valor;
	String tipo;
	String alcance;
	int renglon;
	
	
	public Identificador(String nombre, String valor, String tipo, String alcance, int renglon) {
		super();
		this.nombre = nombre;
		this.valor = valor;
		this.tipo = tipo;
		this.alcance=alcance;
		this.renglon=renglon;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
}