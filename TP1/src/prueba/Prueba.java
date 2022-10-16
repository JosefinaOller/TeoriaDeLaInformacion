package prueba;

import javax.swing.JOptionPane;

import excepciones.SumaException;
import modelo.PrimeraParte;
import modelo.SegundaParte;

public class Prueba
{

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
			p2.entropia();
			p2.mcMillan();
			p2.longitudMedia();
			p2.ordenacion();
			//p2.generaArchivoBinario("3 Caracteres.bin");
			break;
		}
		case 2:
		{
			SegundaParte p25 = new SegundaParte();
			System.out.println("---------------------");
			p25.leeArchivo();
			p25.procesamiento(5);
			p25.entropia();
			p25.mcMillan();
			p25.longitudMedia();
			p25.ordenacion();
			//p25.generaArchivoBinario("5 Caracteres.bin");
			break;
		}
		case 3:
			SegundaParte p27 = new SegundaParte();
			System.out.println("---------------------");
			p27.leeArchivo();
			p27.procesamiento(7);
			p27.entropia();
			p27.mcMillan();
			p27.longitudMedia();
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
