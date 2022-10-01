package prueba;

import javax.swing.JOptionPane;

import excepciones.SumaException;
import modelo.PrimeraParte;
import modelo.SegundaParte;

public class Prueba
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		PrimeraParte p1 = new PrimeraParte();
		SegundaParte p2 = new SegundaParte();
//		p1.leeArchivo();
//		p1.procesamiento();
//		p1.generaMatriz();
//		try
//		{
//			p1.procesamientoMatriz();
//		} catch (SumaException e)
//		{
//			// TODO Auto-generated catch block
//			JOptionPane.showMessageDialog(null, e.getMessage());
//		}
//		p1.memoriaNula();
//		p1.entropia();
//		System.out.println("---------------------");
		p2.leeArchivo();
		p2.procesamiento(3);
		p2.entropia();
		p2.kraft();
		p2.longitudMedia();
		
		
	}

}
