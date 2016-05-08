package net.zyexpress.site.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lumengyu on 2016/3/7.
 */
public class ExcelGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ExcelGenerator.class);

    public static void createExcel(String templateName,OutputStream outputStream, Map<String,Object> dataMap, String encoding) {
        Configuration config = new Configuration();
        File file = new File("/assets/template/");
        config.setClassForTemplateLoading(ExcelGenerator.class,"/assets/template/");
        config.setObjectWrapper(new DefaultObjectWrapper());
        config.setDefaultEncoding("UTF-8");

        Template template;
        try{
            template = config.getTemplate(templateName);
            Writer out = new BufferedWriter(new OutputStreamWriter(outputStream,encoding));
            template.process(dataMap,out);
            out.close();
        }catch(IOException e){
            logger.error(e.toString());
        }catch(TemplateException e){
            logger.error(e.toString());
        }
    }

    public static void createExcel(String templateName, String excelOutputPath, Map<String,Object> dataMap, String encoding) {

        Configuration config = new Configuration();
        System.out.println(ExcelGenerator.class.getClassLoader().getResource("").getPath());
        String templateDirectoryPath = ExcelGenerator.class.getClassLoader().getResource("").getPath();
        File file = new File(templateDirectoryPath+"/assets/template/");
        try{
            config.setDirectoryForTemplateLoading(file);
        }catch (IOException e){
            logger.error(e.toString());
        }
        config.setObjectWrapper(new DefaultObjectWrapper());
        config.setDefaultEncoding("UTF-8");

        Template template;
        try{
            template = config.getTemplate(templateName);
            File outFile = new File(excelOutputPath);
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),encoding));
            template.process(dataMap,out);
            out.close();
        }catch(IOException e){
            logger.error(e.toString());
        }catch(TemplateException e){
            logger.error(e.toString());
        }
    }


}
