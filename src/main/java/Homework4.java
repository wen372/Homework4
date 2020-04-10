
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.File;
import java.util.*;
public class Homework4 {
    public static void main(String[] args)throws Exception{
        Scanner file = new Scanner(new File("data/movies.csv"));
        //skips first line;
        file.nextLine();
        //fills array with Movie nodes containing info about movie
        ArrayList<Movie> arr = new ArrayList<>();
        while(file.hasNextLine()){
            arr.add(Movie.readMovie(file));
        }

        //creates map with title as key and movie node as value
        HashMap<String, Movie> map = new HashMap<>();


        for(Movie movie: arr){
            String[] genreArray = movie.getGenres();
            for(int i=0; i<genreArray.length;i++){
                if(genreArray[i].charAt(0) == ','){
                    String temp = genreArray[i];
                    temp = temp.substring(1);
                    genreArray[i] = temp;
                }
            }


            map.put(movie.getTitle(), movie);
        }

        //gets movie genres,stores them into hashmap as key and adds occurences to value
        HashMap<String, ArrayList<Movie>> genreCount = genres(map);


        //remove blank spacing
        genreCount.remove(" ");



        /*
        //print genre names and count
        for(String genre : genreCount.keySet()){
            //System.out.println(genre);
            System.out.println(genre + "  " +  genreCount.get(genre).size());
            //System.out.println(genreCount.get((genre)));
        }
        */

        //sorts genres hashamp into ascending order
        LinkedHashMap<String,Integer> sortedMap = sortDescendingOrder(genreCount);

        System.out.println("Genre and amount of movies\n");
        for(String genres : sortedMap.keySet()){
            System.out.println(genres + " " + sortedMap.get(genres));
        }
        //System.out.println(sortedMap);


        System.out.println();
        System.out.println();
        HashMap<String, Movie> map2 = removeFromYear(map,5);
        HashMap<String, ArrayList<Movie>> genreCount2 = genres(map2);
        HashMap<String, Integer> genreCount2Sorted = sortDescendingOrder(genreCount2);
        genreCount2Sorted.remove(" ");
        for(String name: genreCount2Sorted.keySet()){
            System.out.println(name + " " + genreCount2Sorted.get(name));
        }


        //key released year, value amt
        HashMap<Integer, Integer> chart = new HashMap<>();
        for(String title: genreCount.keySet()){
            for(Movie movies : genreCount.get(title)){
                if(chart.containsKey(movies.getReleaseYear())){
                    Integer temp = chart.get(movies.getReleaseYear());
                    temp +=1;
                    chart.put(movies.getReleaseYear(),temp);
                }
                else{
                    chart.put(movies.getReleaseYear(),1);
                }
            }
        }


        chart.remove(0);


        int [] releaseYear = new int[chart.size()];
        int[] amountReleased = new int[chart.size()];
        int count = 0;
        for(int year : chart.keySet()){
            releaseYear[count] = year;
            amountReleased[count] = chart.get(year);
            count++;
        }



        System.out.println();

        //System.out.println(chart);
        System.out.println("Year || Amount of movies released");
        for(int num : chart.keySet()){
            System.out.println(num + "  " + chart.get(num));
        }




        XYChart display = new XYChart(500, 400);
        display.setTitle("Release Year");
        display.setXAxisTitle("Year");
        display.setYAxisTitle("Movies Released");
        display.getStyler().setXAxisMin(1900.00);

        display.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        display.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        display.getStyler().setXAxisDecimalPattern("####");
        display.getStyler().setPlotMargin(0);
        display.getStyler().setPlotContentSize(.95);

        XYSeries series = display.addSeries("f(x)", releaseYear, amountReleased);
        series.setMarker(SeriesMarkers.DIAMOND);


        new SwingWrapper(display).displayChart();
        BitmapEncoder.saveBitmap(display, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);



    }




    public static LinkedHashMap<String, Integer> sortDescendingOrder(HashMap<String, ArrayList<Movie>> genres){
        HashMap<String, Integer> map = new HashMap<>();
        for(String genre : genres.keySet()){
            map.put(genre, genres.get(genre).size());
        }
        //System.out.println("Unsorted Map : " + map);

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        //System.out.println("Sorted Map   : " + sortedMap);
        return sortedMap;
    }

    //scans through music nodes and adds occurances to hashmap
    public static HashMap<String, ArrayList<Movie>> genres(HashMap<String, Movie> map){
        HashMap<String, ArrayList<Movie>> genreCount = new HashMap<>();
        for(String movieTitle : map.keySet()){
            //iterate through value node
            for(int i =0; i < map.get(movieTitle).getGenres().length;i ++){

                if(genreCount.containsKey(map.get(movieTitle).getGenres()[i])){
                    String genre = map.get(movieTitle).getGenres()[i];
                    ArrayList<Movie> arrList = genreCount.get(genre);
                    arrList.add(map.get(movieTitle));
                }
                else{
                    ArrayList<Movie> temp = new ArrayList<>();
                    temp.add(map.get(movieTitle));
                    genreCount.put(map.get(movieTitle).getGenres()[i], temp);
                }
            }
        }
        return genreCount;
    }

/*
    public static HashMap<String, Integer> sinceYears(HashMap<String, ArrayList<Movie>> map, int years){
        HashMap<String, ArrayList<Movie>> genreCountSinceYear = new HashMap<>();
        for(String movieTitle : map.keySet()){
            Iterator it = map.get(movieTitle).iterator();
            while(it.hasNext()){
                String genre = it.next().toString();
                if(genreCountSinceYear.containsKey(genre) ){
                    int count = genreCountSinceYear.get(genre);
                }
                else{

                }
            }
        }
        //eturn genreCount;

        return null;
    }

 */

    public static HashMap<String, Movie> removeFromYear(HashMap<String, Movie> map, int year){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int yearCutoff = currentYear - year;
        //test
        System.out.println(yearCutoff + " movies onwards");
        ArrayList<String> namesToRemove = new ArrayList<>();
        for(String names : map.keySet()){
            if(map.get(names).getReleaseYear() < yearCutoff)
                namesToRemove.add(names);
        }
        HashMap<String, Movie> mapCopy = new HashMap<>(map);
        for(String name: namesToRemove){
            mapCopy.remove(name);
        }
        return mapCopy;

    }


}