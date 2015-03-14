package solution.development;

import com.google.gson.Gson;
import com.sun.deploy.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

/**
 * Created by rory on 05/03/15.
 */
public class DataParser {

	public void saveData(MapData mapData, File file) throws FileNotFoundException, UnsupportedEncodingException {

		Gson gson = new Gson();

		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.write(gson.toJson(mapData));
		writer.close();

	}

    public void saveCompatibleFile(MapData mapData, File file) throws FileNotFoundException, UnsupportedEncodingException {

        Gson gson = new Gson();

        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.write(gson.toJson(new CompatibleData(mapData)));
        writer.close();

    }

	public MapData loadData(File file) throws IOException {


		String input = StringUtils.join(Files.readAllLines(file.toPath()),"");

		Gson gson = new Gson();

		return gson.fromJson(input, MapData.class);

	}



}
