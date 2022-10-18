package modelo;

public class NodoArbol
{
	private String clave;
	private double probabilidad;
	private NodoArbol izquierda;
	private NodoArbol derecha;
	private String codigo;
	
	public NodoArbol(String clave, double probabilidad, NodoArbol izquierda, NodoArbol derecha)
	{
		super();
		this.clave = clave;
		this.probabilidad = probabilidad;
		this.izquierda = izquierda;
		this.derecha = derecha;
		this.codigo = null;
	}
	
	public String getClave()
	{
		return clave;
	}
	public void setClave(String clave)
	{
		this.clave = clave;
	}
	public double getProbabilidad()
	{
		return probabilidad;
	}
	public void setProbabilidad(double probabilidad)
	{
		this.probabilidad = probabilidad;
	}
	public NodoArbol getIzquierda()
	{
		return izquierda;
	}
	public void setIzquierda(NodoArbol izquierda)
	{
		this.izquierda = izquierda;
	}
	public NodoArbol getDerecha()
	{
		return derecha;
	}
	public void setDerecha(NodoArbol derecha)
	{
		this.derecha = derecha;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Override
	public String toString() {
		return "[" + clave + ", " + probabilidad +  ", " + codigo + ", " + izquierda
				+ ", " + derecha + "]";
	}
}
