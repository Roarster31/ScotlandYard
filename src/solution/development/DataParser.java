package solution.development;

import com.google.gson.Gson;
import com.sun.deploy.util.StringUtils;
import solution.development.models.DataSave;

import java.io.*;
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

    public DataSave loadV3Data(File file) throws IOException {

        String input = StringUtils.join(Files.readAllLines(file.toPath()),"");

        Gson gson = new Gson();

        return gson.fromJson(input, DataSave.class);
    }

    public void saveV3Data(DataSave data, File file) throws FileNotFoundException, UnsupportedEncodingException {

        Gson gson = new Gson();

        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.write(gson.toJson(data));
        writer.close();

    }
}
