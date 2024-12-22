package org.yourorghere;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.ByteBuffer;
import java.awt.image.*;//пакет графичского интерфейса
import java.io.File;
import javax.imageio.*;


/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer implements GLEventListener {
    GLU glu = new GLU();
    double eyeX=5, eyeY=5, eyeZ=5, viewX=0, viewY=0, viewZ=0, rot=0, speed=0.1, rad=6, lastX, lastY, lastZ, g=0.01;  
    boolean start = true;
    
    
    void SetTreeTexture(GL gl, int num)
    { 
        int wt=256;
        int ht=256;
        try {
                File f = new File ("tree"+num+".jpg");
                BufferedImage img = ImageIO.read (f);
                wt=img.getWidth();
                ht=img.getHeight(); 
                byte tex[] = new byte[wt*ht*4];
                int[] pixel = new int[3];//один пиксель по системе ргб
                
                Raster raster = img.getData();
                int k=0;//счетчик элементов массива tex
                for (int i=0; i<wt;i++)
                {
                    for (int j=0; j<ht;j++)
                    {
                        raster.getPixel(i, j, pixel);//читаем пиксели
                        
                        if(pixel[1]<50)
                        {
                            tex[k]=0;
                            k++;
                            tex[k]=0;
                            k++;
                            tex[k]=0;
                            k++;
                        }
                        else
                        {
                            tex[k]=(byte)pixel[0];
                            k++;
                            tex[k]=(byte)pixel[1];
                            k++;
                            tex[k]=(byte)pixel[2];
                            k++;
                            
                            if ((pixel[0]>120)&&(pixel[1]>120)&&(pixel[2]>120))
                            {
                                tex[k]=0;
                            }
                            else
                            {
                                tex[k]=(byte)255;
                            }
                           
                        } 
                        k++;
                    }
                }
            ByteBuffer btex = ByteBuffer.wrap(tex);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, wt, ht, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, btex);
        
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            
             } catch (IOException ex) {
                Logger.getLogger(GLRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    void Tree (GL gl, float x, float y, int num)
    {
        
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0);
        gl.glAlphaFunc(GL.GL_GREATER, 0.5f);//функция прозрачности
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glColor3d(1,1,1);
        gl.glEnable(GL.GL_TEXTURE_2D);
        SetTreeTexture(gl, num);
        
        gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0, -2.5d, 5);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(0, 2.5d, 5);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(0, 2.5d, 0);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(0, -2.5d, 0);
            
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-2.5d,0, 5);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(2.5d,0, 5);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(2.5d,0, 0);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(-2.5d,0, 0);
            
        gl.glEnd();
        gl.glPopMatrix(); 
        
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_ALPHA_TEST);
    }
    
    
    public void init(GLAutoDrawable drawable) {
        
        start = true;
        
        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        
        
        
        gl.glNewList(120, GL.GL_COMPILE);
        
        float R, alpha, beta, da, db, sbx, sby, sbz;
        int sbn=32;
        R=100;
        db=(float)(Math.PI/sbn);
        da=(float)(2*Math.PI/sbn);
        beta = (float)(-Math.PI/2); //начальное значение` 
        while (beta<=Math.PI/2)
            {
		alpha = 0;
		gl.glBegin(GL.GL_QUAD_STRIP);
		while (alpha <=Math.PI*2+da)
                    {
			sbx=(float)(R*Math.cos(beta)*Math.sin(alpha));
			sby=(float)(R*Math.cos(beta)*Math.cos(alpha));
			sbz=(float)(R*Math.sin(beta));
                        gl.glColor3d(0, Math.cos(beta), 1);
			gl.glVertex3f(sbx,sby,sbz);
			
			sbx=(float)(R*Math.cos(beta+db)*Math.sin(alpha));
			sby=(float)(R*Math.cos(beta+db)*Math.cos(alpha));
			sbz=(float)(R*Math.sin(beta+db));
                        gl.glColor3d(0,Math.cos(beta+db), 1);
			gl.glVertex3f(sbx,sby,sbz);
			alpha = alpha+da;
                    }
		gl.glEnd();
		beta = beta+db;
            }
        
        try {
	File F = new File ("C:\\Users\\Anna\\Documents\\NetBeans_JavaSE_7.4_Portable (1)\\Архитектура_Григорчук.obj"); 
	Scanner SC = new Scanner(F); int n = 0; 
	String s; 
        String faces[];
	String coord[]; 
        
        double cx,cy,cz;
        int face[];
        face = new int[5];
        
	ArrayList<Double> x,y,z; 
	x = new ArrayList();
	y = new ArrayList();
	z = new ArrayList();
        
        
	
	while(SC.hasNext()) 
            {
		s = SC.nextLine();
		
		if ((s.charAt(0)== 'v') && (s.charAt(1)== ' '))
		{
			n++;
			coord = s.split(" "); 
			
			x.add(Double.parseDouble(coord[1]));  
			y.add(Double.parseDouble(coord[2]));
			z.add(Double.parseDouble(coord[3]));
			
		}
        if (s.charAt(0)== 'f')
            {
                faces = s.split(" ");
            
                gl.glColor3d(0, 0, 0);
        
                gl.glBegin(GL.GL_LINE_LOOP);
            
                for (int j=1; j<=faces.length-1; j++)
                    {
                        cx = x.get(Integer.parseInt(faces[j])-1);
                        cy = y.get(Integer.parseInt(faces[j])-1);
                        cz = z.get(Integer.parseInt(faces[j])-1);
                
                        gl.glVertex3d(cx, cz, cy);
                    }
                gl.glEnd();
                
                gl.glColor3d(0.5, 0.5, 0.5);
        
                gl.glBegin(GL.GL_POLYGON);
            
                for (int j=1; j<=faces.length-1; j++)
                    {
                        cx = x.get(Integer.parseInt(faces[j])-1);
                        cy = y.get(Integer.parseInt(faces[j])-1);
                        cz = z.get(Integer.parseInt(faces[j])-1);
                
                        gl.glVertex3d(cx, cz, cy);
                    }
                gl.glEnd();
            
            }
        
            }
        }
        
        catch (FileNotFoundException ex) {
            Logger.getLogger(GLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

	
	
	
	gl.glEndList();
        
         gl.glNewList(122, GL.GL_COMPILE);
        
         try {
	File F = new File ("C:\\Users\\Anna\\Documents\\NetBeans_JavaSE_7.4_Portable (1)\\Арка.obj"); 
	Scanner SC = new Scanner(F); int n = 0; 
	String s; 
        String faces[];
	String coord[]; 
        
        double cx,cy,cz;
        int face[];
        face = new int[5];
        
	ArrayList<Double> x,y,z; 
	x = new ArrayList();
	y = new ArrayList();
	z = new ArrayList();
        gl.glColor3d(1, 0, 0);
        
	
	while(SC.hasNext()) 
            {
		s = SC.nextLine();
		
		if ((s.charAt(0)== 'v') && (s.charAt(1)== ' '))
		{
			n++;
			coord = s.split(" "); 
			
			x.add(Double.parseDouble(coord[1]));  
			y.add(Double.parseDouble(coord[2]));
			z.add(Double.parseDouble(coord[3]));
			
		}
        if (s.charAt(0)== 'f')
            {
                faces = s.split(" ");
            
                
        
                gl.glBegin(GL.GL_LINE_LOOP);
            
                for (int j=1; j<=faces.length-1; j++)
                    {
                        cx = x.get(Integer.parseInt(faces[j])-1);
                        cy = y.get(Integer.parseInt(faces[j])-1);
                        cz = z.get(Integer.parseInt(faces[j])-1);
                
                        gl.glVertex3d(cx, cz, cy);
                    }
                gl.glEnd();
                
                gl.glColor3d(0.5, 0.5, 0.5);
        
                gl.glBegin(GL.GL_POLYGON);
            
                for (int j=1; j<=faces.length-1; j++)
                    {
                        cx = x.get(Integer.parseInt(faces[j])-1);
                        cy = y.get(Integer.parseInt(faces[j])-1);
                        cz = z.get(Integer.parseInt(faces[j])-1);
                        gl.glColor3d(1, 0, 0);
                        gl.glVertex3d(cx, cz, cy);
                    }
                gl.glEnd();
            
            }
        
            }
        }
        
        catch (FileNotFoundException ex) {
            Logger.getLogger(GLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        gl.glEndList();
        
        gl.glNewList(123, GL.GL_COMPILE);
        
         try {
	File F = new File ("C:\\Users\\Anna\\Documents\\NetBeans_JavaSE_7.4_Portable (1)\\камни.obj"); 
	Scanner SC = new Scanner(F); int n = 0; 
	String s; 
        String faces[];
	String coord[]; 
        
        double cx,cy,cz;
        int face[];
        face = new int[5];
        
	ArrayList<Double> x,y,z; 
	x = new ArrayList();
	y = new ArrayList();
	z = new ArrayList();
        
        
	
	while(SC.hasNext()) 
            {
		s = SC.nextLine();
		
		if ((s.charAt(0)== 'v') && (s.charAt(1)== ' '))
		{
			n++;
			coord = s.split(" "); 
			
			x.add(Double.parseDouble(coord[1]));  
			y.add(Double.parseDouble(coord[2]));
			z.add(Double.parseDouble(coord[3]));
			
		}
        if (s.charAt(0)== 'f')
            {
                faces = s.split(" ");
            
                gl.glColor3d(0, 0, 0);
        
                
                
                gl.glColor3d(0.5, 0.5, 0.5);
        
                gl.glBegin(GL.GL_POLYGON);
            
                for (int j=1; j<=faces.length-1; j++)
                    {
                        cx = x.get(Integer.parseInt(faces[j])-1);
                        cy = y.get(Integer.parseInt(faces[j])-1);
                        cz = z.get(Integer.parseInt(faces[j])-1);
                
                        gl.glVertex3d(cx, cz, cy);
                    }
                gl.glEnd();
            
            }
        
            }
        }
        
        catch (FileNotFoundException ex) {
            Logger.getLogger(GLRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        gl.glEndList();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glEnable(GL.GL_DEPTH_TEST);
        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
     
        gl.glLoadIdentity();
        
        glu.gluPerspective(90, 1.33, 0.01, 200);//угол обзора, соотношение сторон экрана, ближнее отсечение, дальнее отсeчение
        glu.gluLookAt(eyeX, eyeY, eyeZ,  viewX, viewY, viewZ,  0, 0, 1);
        
        
        if (start)
        {
            gl.glNewList(121, GL.GL_COMPILE);
            int w, h;
           
            int cw=0,ch=0, cc=0;
            
            File f = new File ("16.jpg");
            
            try {
                
                BufferedImage img = ImageIO.read (f);
                w=img.getWidth();
                h=img.getHeight(); 
                byte tex[] = new byte[w*h*3];
                int[] pixel = new int[3];//один пиксель по системе ргб
                
                Raster raster = img.getData();
                int k=0;//счетчик элементов массива tex
                for (int i=0; i<w;i++)
                {
                    for (int j=0; j<h;j++)
                    {
                        raster.getPixel(i, j, pixel);//читаем пиксели
                        
                        if(pixel[1]<50)
                        {
                            tex[k]=0;
                            k++;
                            tex[k]=0;
                            k++;
                            tex[k]=0;
                            k++;
                        }
                        else
                        {
                            tex[k]=(byte)pixel[0];
                            k++;
                            tex[k]=(byte)pixel[1];
                            k++;
                            tex[k]=(byte)pixel[2];
                            k++;
                        }
                    }
                }
            ByteBuffer btex = ByteBuffer.wrap(tex);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, w, h, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, btex);
        
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glBegin(GL.GL_QUADS);
                gl.glColor3d(1, 1, 1);
                gl.glTexCoord2d(0, 0);
                gl.glVertex3d(100, 100, 0);
                gl.glTexCoord2d(10, 0);
                gl.glVertex3d(-100, 100, 0);
                gl.glTexCoord2d(10, 10);
                gl.glVertex3d(-100, -100, 0);
                gl.glTexCoord2d(0, 10);
                gl.glVertex3d(100, -100, 0);
            gl.glEnd();
        
            gl.glDisable(GL.GL_TEXTURE_2D);
            start = false;
            gl.glEndList();
            } catch (IOException ex) {
                Logger.getLogger(GLRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            
        
        gl.glCallList(121);
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 60, 0);
        gl.glRotatef(180, 0, 0, 1);
        gl.glCallList(120);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0);
        gl.glCallList(122);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 15, 0);
        gl.glCallList(122);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslatef(0, 30, 0);
        gl.glCallList(122);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glScalef(3, 3, 3);
        gl.glCallList(123);
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glScalef(1.5f, 1.5f, 1.5f);
        Tree (gl,7,9,1);
        gl.glPopMatrix();
        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    
    public void KeyPress(int keyCode)
    {
        switch (keyCode)
        {
            case 65: eyeZ=eyeZ+0.1;
                     viewZ= viewZ+0.1;
                     break;
            case 90: eyeZ=eyeZ-0.1; 
                     viewZ= viewZ-0.1;
                     break;
            case 37: rot=rot+0.1;
                     viewX= eyeX+rad*Math.cos(rot); 
                     viewY= eyeY+rad*Math.sin(rot);
                     break;
            case 39: rot=rot-0.1;
                     viewX= eyeX+rad*Math.cos(rot); 
                     viewY= eyeY+rad*Math.sin(rot);
                     break;
            case 38: eyeX=eyeX+speed*Math.cos(rot);
                     eyeY=eyeY+speed*Math.sin(rot);
                     viewX= eyeX+rad*Math.cos(rot); 
                     viewY= eyeY+rad*Math.sin(rot);
                     break;
            case 40: eyeX=eyeX-speed*Math.cos(rot);
                     eyeY=eyeY-speed*Math.sin(rot);
                     viewX= eyeX+rad*Math.cos(rot); 
                     viewY= eyeY+rad*Math.sin(rot);
                     break;
            case 34: viewZ=viewZ-speed;
                     break;
            case 33: viewZ=viewZ+speed;
                     break;
            
        }

    }
        
       
    
    public void MouseMove(int mdx, int mdy) //mouse delta X, mouse delta Y
    {
        rot = rot+speed*mdx;
        viewX= eyeX+rad*Math.cos(rot); 
        viewY= eyeY+rad*Math.sin(rot);
        
        viewZ=viewZ+speed*mdy/10;//вверх, вниз
    }
    
    }


