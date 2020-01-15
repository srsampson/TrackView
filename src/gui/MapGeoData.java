package gui;

import java.util.ArrayList;
import java.util.List;

public final class MapGeoData {

    private final List<MapVector> mapData;
    private final List<MapObject> mapObject;

    public MapGeoData() {
        this.mapData = new ArrayList<>();
        this.mapObject = new ArrayList<>();
    }

    public int getMapVectorCount() {
        synchronized (mapData) {
            return mapData.size();
        }
    }

    public int getMapObjectCount() {
        synchronized (mapObject) {
            return mapObject.size();
        }
    }

    public List<MapVector> getAllMapVectors() {
        List<MapVector> result = new ArrayList<>();

        synchronized (mapData) {
            result.addAll(mapData);
        }

        return result;
    }

    public void addMapVector(MapVector v) {
        synchronized (mapData) {
            try {
                this.mapData.add(v);
            } catch (NullPointerException e) {
                System.err.println("MapGeoData::addMapVector exception during add " + e.toString());
            }
        }
    }

    public List<MapObject> getAllMapObjects() {
        List<MapObject> result = new ArrayList<>();

        synchronized (mapObject) {
            result.addAll(mapObject);
        }

        return result;
    }

    public void addMapObject(MapObject o) {
        synchronized (mapObject) {
            try {
                this.mapObject.add(o);
            } catch (NullPointerException e) {
                System.err.println("MapGeoData::addMapObject exception during add " + e.toString());
            }
        }
    }
}