import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
/**
* @author WuZy 
* @version 创建时间：2018年4月7日 上午9:06:55
* Copyright (C) 2018 wuzy
*/
//=>1.建立owl实体模型
//=>2.获取本体对应的url
//=>3.建立编写sparql查询语句
//=>4.输出查询结果

public class myJena{

	Model model;//定义本体模型
    int count = 0;
    String url=null;
	private BufferedReader bufferedReader;

	//sparql查询语句
	public boolean queryBySparql(String selectStr, String path) throws IOException {
		model = ModelFactory.createDefaultModel();
		loadModel(path);
		this.getURl(path);
        String queryStr = "PREFIX rdf:<"+url+">"
						+ "Select ?s ?p ?o "
						+ "where{?s ?p ?o."
        				+ "FILTER regex(?o,'叶')}";
        
       /* String queryStr = "PREFIX rdf:<"+url+">"
						+ "Select ?p ?o "
						+"where{rdf:" + selectStr + " ?p ?o."								
        						+"FILTER regex(?o,'茶叶')}";*/
        
        /* String queryStr = "PREFIX rdf:<"+url+">"
						+ "Select ?rdf ?p ?o "
						+"where{?rdf ?p ?o."
						+ "FILTER regex(?o,'叶')}";		*/		
		
        /*String queryStr = "PREFIX rdf:<"+url+">"
						+ "Select ?rdf ?s ?p ?o "
						+"where{rdf:" + selectStr + " ?p ?o.}";*/
        
		/*String queryStr3="PREFIX rdf:<http://www.semanticweb.org/ontologies/2012/11/4/茶树虫害.owl#>"+
				"Select ?p ?o where{rdf:"+"查询的对象"+" ?p ?o.}";*/
		Query query = QueryFactory.create(queryStr);
		QueryExecution queryResult = QueryExecutionFactory.create(query, model);
		ResultSet results = queryResult.execSelect();
		FileWriter resultFile = null;
		String rdf, sub, pro, obj;
		int rdfIndex, subIndex, proIndex, objIndex;
		try {
			//检测储存文件是否存在
			File f = new File("//home//wuzy//graduation//selectResult.txt");
			if (!f.exists()) {

			} else {
				f.delete();
			}
			resultFile = new FileWriter("//home//wuzy//graduation//selectResult.txt",
					true);
			while (results.hasNext()) {
				count++;
				QuerySolution soln = results.nextSolution();// 查询结果中的每一条（称之为满足条件的一个solution）
				//rdf
				//rdf = soln.get("rdf").toString();
				
				//subject
				//sub = soln.get("s").toString();
				
				//property
				pro = soln.get("p").toString();
				proIndex = pro.indexOf("#");
				pro = pro.substring(proIndex + 1);
				//object
				obj = soln.get("o").toString();
				objIndex = obj.indexOf("#");
				obj = obj.substring(objIndex + 1);
				/*System.out.println(selectStr);
				System.out.println(pro);
				System.out.println(obj+ "\n");*/
				resultFile.write("s:  " + selectStr + "\t");
				resultFile.write("p:  " + pro + "\t");						
				resultFile.write("o:  " + obj + "\n");
				resultFile.flush();			
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (resultFile != null) {
				try {
					resultFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		while (results.hasNext()) {
			count++;			
		}
		System.out.print("查询到数量"+count);
		results = null;
		queryResult.close();
		return true;
	}

	//加载本体文件
	private void loadModel(String path) {
		InputStreamReader in;
		try {
			FileInputStream file = new FileInputStream(path);
			in = new InputStreamReader(file, "UTF-8");// 处理中文		
			model.read(in, null);
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("无法打开本体文件，程序将终止");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	// 获取到本体对应的URL
	public void getURl(String path) throws IOException {
		int index;
		File file = new File(path);
		InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), "UTF-8");// 考虑到编码格式
		bufferedReader = new BufferedReader(read);
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains("xmlns=\"")) {				
				url = line;
				index = url.indexOf("\"");
				url = url.substring(index + 1,url.length()-1);
				System.out.println(url);
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		myJena myLearn=new myJena();		
		Scanner scanner = new Scanner(System.in);   
		System.out.println("请输入要查询的语句：");                  
		String selectStr = scanner.next();                      
		String owlPath = "//home//wuzy//graduation//tea2.owl";
		/*String queryStr="PREFIX rdf:<http://www.semanticweb.org/ontologies/2012/11/4/茶树虫害.owl#>"+
				"Select ?p ?o where{rdf:"+result+" ?p ?o.}";*/
		try {
			myLearn.queryBySparql(selectStr, owlPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
