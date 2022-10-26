package prueba;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import excepciones.SumaException;
import modelo.PrimeraParte;
import modelo.SegundaParte;

public class Prueba
{
	static double entropia,longitudMedia,rendimiento,redundancia;
	static String[] kraft = {};
	static String compacto;
	static DecimalFormat df = new DecimalFormat("#.####");
	
	public static void main(String[] args)
	{
		String[] options = new String[] { "Parte 1", "Parte 2 - 3 caracteres", "Parte 2 - 5 caracteres",
				"Parte 2 - 7 Caracteres", "Terminar" };
		int option = JOptionPane.showOptionDialog(null, "\u00bfQu\u00e9 hacemos?", "Elegir", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		do {
		switch (option)
		{
		case 0:
		{
			PrimeraParte p1 = new PrimeraParte();
			p1.leeArchivo();
			p1.procesamiento();
			p1.generaMatriz();
			try
			{
				p1.procesamientoMatriz();
			} catch (SumaException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			p1.memoriaNula();
			p1.entropia();
			break;
		}
		case 1:
		{
			SegundaParte p2 = new SegundaParte();
			System.out.println("---------------------");
			p2.leeArchivo();
			p2.procesamiento(3);
			entropia=p2.entropia();
			kraft= p2.mcMillan().split("/");
			longitudMedia=3;
			compacto=p2.compacto(3);
			rendimiento = entropia/longitudMedia;
			redundancia = 1 - rendimiento;
			p2.ordenacion();
			p2.huffman();
			JOptionPane.showMessageDialog(null,String.format("<html><body width='%1s'>Entropía: "+entropia+"<p>Desigualdad de kraft: "+kraft[0]+"</p>"+kraft[1]+"<p>Longitud media: "+df.format(longitudMedia)+"<p>"+compacto+"</p>"+"</p>"+"<p>Rendimiento: "+df.format(rendimiento)+"</p>"+"<p>Redundancia: "+df.format(redundancia)+"</p>", 200, 200));
			//p2.generaArchivoBinario("3 Caracteres.bin");
			break;
		}
		case 2:
		{
			SegundaParte p25 = new SegundaParte();
			System.out.println("---------------------");
			p25.leeArchivo();
			p25.procesamiento(5);
			entropia=p25.entropia();
			kraft= p25.mcMillan().split("/");
			longitudMedia=5;
			compacto=p25.compacto(5);
			rendimiento = entropia/longitudMedia;
			redundancia = 1 - rendimiento;
			p25.ordenacion();
			p25.huffman();
			JOptionPane.showMessageDialog(null,String.format("<html><body width='%1s'>Entropía: "+entropia+"<p>Desigualdad de kraft: "+kraft[0]+"</p>"+kraft[1]+"<p>Longitud media: "+df.format(longitudMedia)+"<p>"+compacto+"</p>"+"</p>"+"<p>Rendimiento: "+df.format(rendimiento)+"</p>"+"<p>Redundancia: "+df.format(redundancia)+"</p>", 200, 200));			
			p25.ordenacion();
			//p25.generaArchivoBinario("5 Caracteres.bin");
			break;
		}
		case 3:
			SegundaParte p27 = new SegundaParte();
			System.out.println("---------------------");
			p27.leeArchivo();
			p27.procesamiento(7);
			entropia=p27.entropia();
			kraft= p27.mcMillan().split("/");
			longitudMedia=7;
			compacto=p27.compacto(7);
			rendimiento = entropia/longitudMedia;
			redundancia = 1 - rendimiento;
			p27.ordenacion();
			p27.huffman();
			JOptionPane.showMessageDialog(null,String.format("<html><body width='%1s'>Entropía: "+entropia+"<p>Desigualdad de kraft: "+kraft[0]+"</p>"+kraft[1]+"<p>Longitud media: "+df.format(longitudMedia)+"<p>"+compacto+"</p>"+"</p>"+"<p>Rendimiento: "+df.format(rendimiento)+"</p>"+"<p>Redundancia: "+df.format(redundancia)+"</p>", 200, 200));
			p27.ordenacion();
			//p27.generaArchivoBinario("7 Caracteres.bin");
			break;
		default:
			break;
		}
		option = JOptionPane.showOptionDialog(null, "\u00bfQu\u00e9 hacemos?", "Elegir", JOptionPane.DEFAULT_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		}while (option!=4);
	}

}
