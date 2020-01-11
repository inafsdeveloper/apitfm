package com.pnafs.okhttp.util;

import java.util.Random;

public class RandomWordPair {
    private static final String[] RANDOM_WORD_LIST = new String[] {
            "Adult","Aeroplane","Air","Aircraft-Carrier","Air-force","Airport","Album","Alphabet","Apple","Arm","Army",
            "Baby","Backpack","Balloon","Banana","Bank","Barbecue","Bathroom","Bathtub","Bed","Bee","Bible","Bird","Book","Boss","Bottle","Bowl","Box","Boy","Brain","Bridge","Butterfly","Button",
            "Cappuccino","Car","Car-race","Carpet","Carrot","Cave","Chair","ChessBoard","Chief","Child","Chisel","Chocolates","Church","Church","Circle","Circus","Clock","Clown","Coffee","Coffee-shop","Comet","Compact-Disc","Compass","Computer","Crystal","Cup","Cycle",
            "Data Base","Desk","Diamond","Dress","Drill","Drink","Drum","Dung",
            "Ears","Earth","Egg","Electricity","Elephant","Eraser","Explosive","Eyes",
            "Family","Fan","Feather","Festival","Film","Finger","Fire","Floodlight","Flower","Foot","Fork","Freeway","Fruit","Fungus",
            "Game","Garden","Gas","Gate","Gemstone","Girl","Gloves","God","Grapes","Guitar",
            "Hammer","Hat","Hieroglyph","Highway","Horoscope","Horse","Hose",
            "Ice","Ice-cream","Insect",
            "Jet fighter","Junk",
            "Kaleidoscope","Kitchen","Knife",
            "Leather-jacket","Leg","Library","Liquid",
            "Magnet","Man","Map","Maze","Meat","Meteor","Microscope","Milk","Milkshake","Mist","Money","Monster","Mosquito","Mouth",
            "Nail","Navy","Necklace","Needle",
            "Onion",
            "PaintBrush","Pants","Parachute","Passport","Pebble","Pendulum","Pepper","Perfume","Pillow","Plane","Planet","Pocket","Post-office","Potato","Printer","Prison","Pyramid",
            "Radar","Rainbow","Record","Restaurant","Ring","Robot","Rock","Rocket","Roof","Room","Rope",
            "Saddle","Salt","Sandpaper","Sandwich","Satellite","School","Sex","Ship","Shoes","Shop","Shower","Signature","Skeleton","Slave","Snail","Software","Solid","Space Shuttle","Spectrum","Sphere","Spice","Spiral","Spoon","Sports-car","Spot Light","Square","Staircase","Star","Stomach","Sun","Sunglasses","Surveyor","Swimming Pool","Sword",
            "Table","Tapestry","Teeth","Telescope","Television","Tennis-racquet","Thermometer","Tiger","Toilet","Tongue","Torch","Torpedo","Train","Treadmill","Triangle","Tunnel","Typewriter",
            "Umbrella",
            "Vacuum","Vampire","Videotape","Vulture",
            "Water","Web","Wheelchair","Window","Worm",
            "X-ray"
    };

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static String get() {
        int uniqueInteger = RANDOM.nextInt(100);
        return RANDOM_WORD_LIST[RANDOM.nextInt(RANDOM_WORD_LIST.length)] + "-" + uniqueInteger + "-"
                + RANDOM_WORD_LIST[RANDOM.nextInt(RANDOM_WORD_LIST.length)];
    }
}
