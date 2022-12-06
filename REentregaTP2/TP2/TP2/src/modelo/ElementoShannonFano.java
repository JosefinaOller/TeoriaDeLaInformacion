package modelo;

public class ElementoShannonFano {
	@Override
	public String toString() {
		return "ElementoShannonFano [clave=" + clave + ", probabilidad=" + probabilidad + ", codigo=" + codigo + "]";
	}

	private String clave;
	private double probabilidad;
	private String codigo;
	
	public ElementoShannonFano(String clave,double probabilidad) {
		this.clave=clave;
		this.probabilidad=probabilidad;
		this.codigo="";
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public double getProbabilidad() {
		return probabilidad;
	}

	public void setProbabilidad(double probabilidad) {
		this.probabilidad = probabilidad;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
