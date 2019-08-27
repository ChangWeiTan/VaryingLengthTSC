#!/bin/bash 
PROJECTDIR=$PWD/
OUTPUTDIR="/home/ubuntu/workspace/Dataset/"
DATASETDIR="/home/ubuntu/workspace/Dataset/UCRArchive_2018/"
if [ ! -d "$DATASETDIR" ]; then
      DATASETDIR="/mnt/c/Users/cwtan/workspace/Dataset/UCRArchive_2018/"
fi
if [ ! -d "$DATASETDIR" ]; then
      DATASETDIR="/mnt/lustre/projects/ud82/changt/workspace/Dataset/"
fi

if [ ! -d "$OUTPUTDIR" ]; then
      OUTPUTDIR="/mnt/c/Users/cwtan/workspace/Dataset/"    
fi
if [ ! -d "$OUTPUTDIR" ]; then
      OUTPUTDIR="/mnt/lustre/projects/ud82/changt/workspace/Dataset/"
fi

DATASETS=("ItalyPowerDemand" "SonyAIBORobotSurface1" "Coffee" "ECG200"
            "BeetleFly" "BirdChicken" "SonyAIBORobotSurface2" "Wine" "GunPoint" "TwoLeadECG" "MoteStrain" "Beef"
            "Plane" "FaceFour" "OliveOil" "SyntheticControl" "DistalPhalanxOutlineAgeGroup" "DistalPhalanxTW"
            "ECGFiveDays" "MiddlePhalanxTW" "MiddlePhalanxOutlineAgeGroup" "ArrowHead" "CBF" "Lightning7"
            "ProximalPhalanxOutlineAgeGroup" "ProximalPhalanxTW" "ToeSegmentation2" "DiatomSizeReduction"
            "ToeSegmentation1" "Meat" "Trace" "ShapeletSim" "DistalPhalanxOutlineCorrect" "Herring"
            "MiddlePhalanxOutlineCorrect" "ProximalPhalanxOutlineCorrect" "Car" "Lightning2" "Ham" "MedicalImages"
            "Symbols" "Adiac" "SwedishLeaf" "FISH" "FacesUCR" "OSULeaf" "PhalangesOutlinesCorrect" "Worms"
            "WormsTwoClass" "Earthquakes" "WordSynonyms" "Strawberry"
            "CricketX" "CricketY" "CricketZ" "FiftyWords" "FaceAll" "InsectWingbeatSound" "Computers"
            "ECG5000" "ChlorineConcentration" "Haptics" "TwoPatterns" "LargeKitchenAppliances"
            "RefrigerationDevices" "ScreenType" "SmallKitchenAppliances" "ShapesAll" "Mallat" "Wafer"
            "CinCECGTorso" "Yoga" "InlineSkate" "WordSynonyms" "UWaveGestureLibraryX" "UWaveGestureLibraryY"
            "UWaveGestureLibraryZ" "Phoneme" "ElectricDevices" "FordB" "FordA" "NonInvasiveFetalECGThorax1"
            "NonInvasiveFetalECGThorax2" "HandOutlines" "UWaveGestureLibraryAll" "StarLightCurves")

echo Compiling javac -sourcepath src -d bin -cp $PWD/lib/*: src/**/*.java 
javac -sourcepath src -d bin -cp $PWD/lib/*: src/**/*.java

cd bin 
echo Current Directory: $PWD
echo Dataset Directory: $DATASETDIR

PERCENTAGE=0.85

for problem in "${DATASETS[@]}"; do
    java -Xmx14g -Xms14g -cp $PROJECTDIR/lib/*: datasetGenerator.UniformSampling $OUTPUTDIR $DATASETDIR $problem $PERCENTAGE 1234567
    java -Xmx14g -Xms14g -cp $PROJECTDIR/lib/*: datasetGenerator.Prefix $OUTPUTDIR $DATASETDIR $problem $PERCENTAGE 2345678
    java -Xmx14g -Xms14g -cp $PROJECTDIR/lib/*: datasetGenerator.Suffix $OUTPUTDIR $DATASETDIR $problem $PERCENTAGE 3456789
    java -Xmx14g -Xms14g -cp $PROJECTDIR/lib/*: datasetGenerator.Subsequence $OUTPUTDIR $DATASETDIR $problem $PERCENTAGE 4567890
    java -Xmx14g -Xms14g -cp $PROJECTDIR/lib/*: datasetGenerator.NonUniformSampling $OUTPUTDIR $DATASETDIR $problem $PERCENTAGE 678901
done

