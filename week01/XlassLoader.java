package mm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class XlassLoader extends ClassLoader {

    public static void main(String[] args) throws Exception {
        final String path = "/Users/yangtianrui/Downloads/Hello.xlass";
        final XlassLoader loader = new XlassLoader();
        final Class<?> clazz = loader.loadClass(path);
        
        System.out.println(clazz.getName());
        System.out.println(Arrays.toString(clazz.getDeclaredMethods()));
        /*
            输出：
            Hello
            [public void Hello.hello()]
         */
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final File file = new File(name);
        if (!file.exists()) {
            throw new ClassNotFoundException("找不到这个xlass文件，无法加载类");
        }
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(name));
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (255 - bytes[i]);
            }
            final String className = file.getName().split("\\.")[0];
            return defineClass(className, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
