package com.nomaf.refferal.libs;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
 
/** SLAPI = Saving/Loading API
 * API for Saving and Loading Objects.
 * @author Tomsik68
 */
public class SLAPI
{
    public static void save(Object obj, File file) throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, true));
        oos.writeObject(obj);
        oos.flush();
        oos.close();
    }
    public static Object load(File file) throws Exception
    {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object result = ois.readObject();
        ois.close();
        return result;
    }
}