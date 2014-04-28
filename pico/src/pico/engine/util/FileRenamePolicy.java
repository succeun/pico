package pico.engine.util;

import java.io.File;
import java.io.IOException;

/**
 * 저장될때 이름명이 겹칠 수 있으므로, 새로운 파일명을 얻어온다.
 * @author Eun Jeong-Ho, silver@intos.biz
 * @since 2005. 5. 25.
 */
public class FileRenamePolicy implements RenamePolicy
{

    public FileRenamePolicy()
    {
        super();
    }

    public File rename(File f)
    {
        if(createNewFile(f))
            return f;
        String name = f.getName();
        String body = null;
        String ext = null;
        int dot = name.lastIndexOf(".");
        if(dot != -1)
        {
            body = name.substring(0, dot);
            ext = name.substring(dot);
        } else
        {
            body = name;
            ext = "";
        }
        String newName;
        for(int count = 0; !createNewFile(f) && count < 9999; f = new File(f.getParent(), newName))
        {
            count++;
            newName = body + count + ext;
        }

        return f;
    }

    private boolean createNewFile(File f)
    {
        try
        {
            return f.createNewFile();
        }
        catch(IOException ignored)
        {
            return false;
        }
    }
}

