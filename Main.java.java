import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		List<List<String>> list = readValues();
		// System.out.println(list.get(0));
		List<Integer> valueList = new ArrayList<Integer>();
		List<Integer> weightList = new ArrayList<Integer>();
		for (int i = 1; i < list.size(); i++) {
			valueList.add(Integer.parseInt(list.get(i).get(4)));
			weightList.add(Integer.parseInt(list.get(i).get(5)));
		}
		List<List<String>> list1 = readSequential();
		List<ArrayList<Double>> sequential_data = new ArrayList<ArrayList<Double>>();
		for (int i = 1; i < list1.size(); i++) {
			ArrayList<Double> row = new ArrayList<>();
			for (int j = 1; j < list1.get(0).size(); j++) {
				row.add(Double.parseDouble(list1.get(i).get(j)));
			}
			sequential_data.add(row);
		}
		// System.out.println(sequential_data.get(0).get(1));
		long start = System.nanoTime();

		List<Track> tracks = new ArrayList<Track>();
		createTracks(tracks, valueList, weightList, sequential_data);
		List<Track> album = new ArrayList<>();
		Algorithm1(album, tracks);

		int t = 0;
		for (int i = 0; i < album.size(); i++) {
			t += album.get(i).track_duration;
		}
		System.out.println("Used Time: " + t);
		double obj = 0;
		for (int i = 0; i < album.size(); i++) {
			obj += album.get(i).track_individual_value;
		}
		obj -= 0.02 * (1800 - t);
		System.out.println("Objective: " + obj);
		System.out.println();
		List<Integer> track_ids = new ArrayList<>();

		for (int i = 0; i < album.size(); i++) {
			track_ids.add(album.get(i).track_id);
		}

		int[] trackOrder = new int[album.size()];
		trackOrder = Algorithm2(track_ids, album);

		System.out.println("ORDER:");
		for (int i = 0; i < trackOrder.length; i++) {
			System.out.println(trackOrder[i]);
		}
		long elapsedTime = System.nanoTime() - start;
		double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;

		System.out.println("runtime: " + elapsedTimeInSecond);
	}

	private static void Algorithm1(List<Track> album, List<Track> tracks) {
		int CapacityS = 30 * 60;
		List<Integer> usedTracks = new ArrayList<>();
		while (CapacityS > 0) {
			Double maxValue = Double.MIN_VALUE;
			int maxIndex = -1;
			for (int i = 0; i < tracks.size(); i++) {
				if (!usedTracks.contains(i)
						&& (double) tracks.get(i).track_individual_value
								/ (double) tracks.get(i).track_duration > maxValue
						&& tracks.get(i).track_duration < CapacityS) {
					maxIndex = i;
					maxValue = (double) tracks.get(i).track_individual_value / (double) tracks.get(i).track_duration;
				}
			}
			if (maxIndex == -1) {
				break;
			} else {
				album.add(tracks.get(maxIndex));
				usedTracks.add(maxIndex);
				CapacityS = CapacityS - tracks.get(maxIndex).track_duration;
			}
		}
	}

	private static int[] Algorithm2(List<Integer> track_ids, List<Track> album) {
		int size = album.size();
		int[] trackOrder = new int[size];
		int LargestA = 0;
		int LargestB = 0;
		int LargestA_index = -1;
		int LargestB_index = -1;
		for (int i = 0; i < size; i++) {
			if (album.get(i).track_individual_value > LargestA) {
				LargestB = LargestA;
				LargestB_index = LargestA_index;
				LargestA = album.get(i).track_individual_value;
				LargestA_index = i;
			} else if (album.get(i).track_individual_value > LargestB) {
				LargestB = album.get(i).track_individual_value;
				LargestB_index = i;
			}
		}
		int idA = track_ids.get(LargestA_index);
		int idB = track_ids.get(LargestB_index);
		trackOrder[0] = album.get(LargestA_index).track_id;
		trackOrder[size - 1] = album.get(LargestB_index).track_id;
		track_ids.remove(track_ids.indexOf(idA));
		track_ids.remove(track_ids.indexOf(idB));
		for (int i = 0; i < size - 2; i++) {
			for (int j = 0; j < album.size(); j++) {
				if (album.get(j).track_id == trackOrder[i]) {
					Double largestSeq = 0.0;
					int largestId = -1;
					for (int k : track_ids) {
						if (album.get(j).track_sequential_value[k] > largestSeq) {
							largestSeq = album.get(j).track_sequential_value[k];
							largestId = k;
						}
					}
					trackOrder[i + 1] = largestId;
					track_ids.remove(track_ids.indexOf(largestId));
				}
			}
		}
		return trackOrder;
	}

	private static void createTracks(List<Track> tracks, List<Integer> valueList, List<Integer> weightList,
			List<ArrayList<Double>> sequential_data) {
		for (int i = 0; i < valueList.size(); i++) {
			int size = sequential_data.get(i).size();
			double[] array = new double[size];
			for (int j = 0; j < size; j++) {
				array[j] = sequential_data.get(i).get(j);
			}
			tracks.add(new Track(i, Math.round((float) weightList.get(i) / 1000), valueList.get(i), array));
		}
	}

	public static List<List<String>> readValues() throws IOException {
		try {
			List<List<String>> data = new ArrayList<>();// list of lists to store data
			String file = "term_project_value_data.csv";// file path
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			// Reading until we run out of lines
			String line = br.readLine();
			while (line != null) {
				List<String> lineData = Arrays.asList(line.split(","));// splitting lines
				data.add(lineData);
				line = br.readLine();
			}

			// printing the fetched data
			/*
			 * for(List<String> list : data)
			 * {
			 * for(String str : list)
			 * System.out.print(str + " ");
			 * System.out.println();
			 * }
			 */
			br.close();
			return data;
		} catch (Exception e) {
			System.out.print(e);
			List<List<String>> data = new ArrayList<>();// list of lists to store data
			return data;
		}

	}

	public static List<List<String>> readSequential() throws IOException {
		try {
			List<List<String>> data = new ArrayList<>();// list of lists to store data
			String file = "term_project_sequential_data.csv";// file path
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			// Reading until we run out of lines
			String line = br.readLine();
			while (line != null) {
				List<String> lineData = Arrays.asList(line.split(","));// splitting lines
				data.add(lineData);
				line = br.readLine();
			}

			// printing the fetched data
			/*
			 * for(List<String> list : data)
			 * {
			 * for(String str : list)
			 * System.out.print(str + " ");
			 * System.out.println();
			 * }
			 */
			br.close();
			return data;
		} catch (Exception e) {
			System.out.print(e);
			List<List<String>> data = new ArrayList<>();// list of lists to store data
			return data;
		}

	}

}
