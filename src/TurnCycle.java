/**
 * Created by adam on 28/03/2016.
 */

import java.util.ArrayList;

/** Manages the sequence of players, as well as the different phases of each turn*/
public class TurnCycle {

    public static void play(){
        //while a winner doesnt exist
        while(!Player.doesWinnerExist()){
            Player currPlayer = Player.players.get(Player.currentPlayer);
            int reinforcements = calculateReinforcements();
            currPlayer.reinforcements = reinforcements;
            Main.GameFrame.Map.PlayerNamesBar.putPlayerNames();
            GameFrame.SideBar.log(currPlayer.name + "'s turn\n", GameFrame.SideBar.info);
            GameFrame.SideBar.log("You received " + reinforcements + " backup reinforcements\n", GameFrame.SideBar.info);

            currPlayer.allocateReinforcements(reinforcements);
            //attack here

            if(currPlayer.isHuman()){
                attackSequence();
                fortify();
            }




            Player.nextPlayer();
        }
    }

    public static int calculateReinforcements(){
        int reinforcementsToAllocate = 0;
        int[] continentsControlled = {0, 0, 0, 0, 0, 0};
        ArrayList<Country> territories = Player.players.get(Player.currentPlayer).getOwnedTerritories();

        //allocate reinforcements based on num territories controlled
        int tmp = Math.floorDiv(territories.size(), 3);
        if (tmp <= 3){
            reinforcementsToAllocate += 3;
        } else {
            reinforcementsToAllocate += tmp;
        }

        //check to see does a player control a continent
        int j=0;
        for (int i=0; i<6; i++){
            boolean ownsAll = true;
            while (j < Constants.CONTINENT_IDS.length && Constants.CONTINENT_IDS[j] == i) {
                if (Country.countries.get(j).getOwner().index != Player.currentPlayer){
                    ownsAll = false;
                }
                j++;
            }
            if (ownsAll == true){
                reinforcementsToAllocate += Constants.CONTINENT_VALUES[i];
            }
        }

        return reinforcementsToAllocate;
    }

    public static void attackSequence(){
        Player currPlayer = Player.players.get(Player.currentPlayer);
        int attackIndex = -1;
        int defendIndex = -1;

        GameFrame.SideBar.log("ATTACK!\nIf you do not wish to attack, type skip\n", GameFrame.SideBar.info);
        GameFrame.SideBar.log("Select a country to attack from\n",GameFrame.SideBar.prompt);
        String attackerName = GameFrame.SideBar.getCommand();
        if(!attackerName.toLowerCase().contains("skip")){
            while(attackIndex < 0){
                attackIndex = Country.getCountry(attackerName);
                //if user entered correct country
                if(attackIndex >= 0){
                    Country attackCountry = Country.countries.get(attackIndex);
                    //if the current player owns the country
                    if(attackCountry.getOwner() == currPlayer){
                        //check if country has sufficient troops.
                        if(attackCountry.troops > 1){
                            GameFrame.SideBar.log("Please select the territory you wish to attack\n", GameFrame.SideBar.prompt);
                            String defenderName = GameFrame.SideBar.getCommand();
                            defendIndex = -1;
                            while(defendIndex < 0){
                                defendIndex = Country.getCountry(defenderName);
                                //if user entered a country
                                if(defendIndex >= 0){
                                    Country defendCountry = Country.countries.get(defendIndex);
                                    //check that player DOESNT own defendCountry
                                    if(defendCountry.getOwner() != currPlayer){
                                        //if countries are adjacent
                                        if(attackCountry.isAdjacent(defendCountry)){
                                            int numTroops = -1;
                                            int maxAttackTroops;
                                            if(attackCountry.troops <= 3){
                                                maxAttackTroops = attackCountry.troops - 1;
                                            }else{
                                                maxAttackTroops = 3;
                                            }
                                            GameFrame.SideBar.log("How many troops would you like to attack with?.\n", GameFrame.SideBar.prompt);
                                            while(numTroops < 0){
                                                numTroops = Integer.parseInt(GameFrame.SideBar.getCommand());
                                                //numTroops must be less than the number of troops on the territory (as at least one has to stay behindg
                                                if(numTroops > 1 && numTroops <= maxAttackTroops && numTroops < attackCountry.troops){
                                                    attackCountry.attack(defendCountry, numTroops);

                                                }else{
                                                    GameFrame.SideBar.log("That is an invalid number of troops to attack with. You can attack with a max of " + maxAttackTroops + " troops. Try again:\n", GameFrame.SideBar.error);
                                                    numTroops = -1;
                                                }
                                            }
                                        }else{
                                            GameFrame.SideBar.log("These territories are not adjacent. Please select two territories that are.\n", GameFrame.SideBar.error);
                                            attackIndex = -1; // go back to beginning - select attacker country
                                            GameFrame.SideBar.log("Select a country to attack from", GameFrame.SideBar.prompt);
                                            attackerName = GameFrame.SideBar.getCommand();
                                        }
                                    }else{
                                        GameFrame.SideBar.log("You own this territory. Please select one that you do not currently control.\n", GameFrame.SideBar.error);
                                        defendIndex = -1;
                                        attackerName = GameFrame.SideBar.getCommand();
                                    }
                                }else{
                                    GameFrame.SideBar.log("That doesn't appear to be a territory. Please enter a valid territory name.\n", GameFrame.SideBar.error);
                                }
                            }

                        }else{
                            GameFrame.SideBar.log("This territory does not have sufficient troops to launch an attack. Please choose another, or type 'skip' to proceed. \n", GameFrame.SideBar.error);
                            attackIndex = -1;
                            GameFrame.SideBar.getCommand();
                        }
                    }else{
                        GameFrame.SideBar.log("You do not own " + Country.countries.get(attackIndex).name + ". Please select one that you currently control.\n", GameFrame.SideBar.error);
                        attackIndex = -1; //to go through loop again
                        GameFrame.SideBar.getCommand();
                    }
                }else{
                    GameFrame.SideBar.log("That doesn't appear to be a territory. Please enter a valid territory name.\n", GameFrame.SideBar.error);
                    GameFrame.SideBar.getCommand();
                }
            }
        }
    }

    public static void fortify(){
        boolean complete = false;
        while (!complete){
            //figure out way to check if there exists an link between two chosen territories
            GameFrame.SideBar.log("Please enter a territory name you wish to fortify.\n", GameFrame.SideBar.prompt);
            String toBeFortified = GameFrame.SideBar.getCommand();
            //check if input is a country
            if (!(toBeFortified.toLowerCase().contains("skip"))) {
                int x;
                int y;
                if ((x = Commands.isCountry(toBeFortified)) >= 0) {
                    //check if player owns the country
                    if (Country.countries.get(x).getOwner().index == Player.currentPlayer) {
                        GameFrame.SideBar.log("Please enter the name of the territory you wish to fortify from.\n", GameFrame.SideBar.prompt);
                        String fortifyFrom = GameFrame.SideBar.getCommand();
                        if ((y = Commands.isCountry(fortifyFrom)) >= 0) {
                            //check if player owns the country
                            if (Country.countries.get(y).getOwner().index == Player.currentPlayer) {
                                //check if there are sufficient troops
                                if (Country.countries.get(y).getTroopCount() > 1) {
                                    //check is there a link
                                    if (adjacencyCheck(Country.countries.get(x), Country.countries.get(y))) {
                                        GameFrame.SideBar.log("How many troops do you wish to fortify with?.\n", GameFrame.SideBar.prompt);
                                        int troops = Integer.parseInt(GameFrame.SideBar.getCommand());
                                        if (troops > 0 && troops < Country.countries.get(y).getTroopCount()) {
                                            Country.countries.get(y).removeTroops(troops);
                                            Country.countries.get(x).addTroops(troops);
                                            complete = true;
                                        } else {
                                            GameFrame.SideBar.log("That is an invalid number of troops. Please try again.\n", GameFrame.SideBar.error);
                                        }
                                    } else {
                                        GameFrame.SideBar.log("You do not control a path between these two territories. Please make a new selection.\n", GameFrame.SideBar.error);
                                    }
                                } else {
                                    GameFrame.SideBar.log("This territory does not have sufficient troops with which to fortify. Please choose another, or type 'skip' to proceed. \n", GameFrame.SideBar.error);
                                }
                            } else {
                                GameFrame.SideBar.log("You do not own this territory. Please select one that you currently control.\n", GameFrame.SideBar.error);
                            }
                        } else {
                            GameFrame.SideBar.log("That doesn't appear to be a territory. Please enter a valid territory name.\n", GameFrame.SideBar.error);
                        }
                    } else {
                        GameFrame.SideBar.log("You do not own this territory. Please select one that you currently control.\n", GameFrame.SideBar.error);
                    }
                } else {
                    GameFrame.SideBar.log("That doesn't appear to be a territory. Please enter a valid territory name.\n", GameFrame.SideBar.error);
                }
            } else {
                complete = true;
            }
        }
    }

    public static boolean adjacencyCheck(Country c1, Country c2){

        for (int index: Constants.ADJACENT[c1.getIndex()]) {
            if (Country.countries.get(index).getOwner().index == Player.currentPlayer){
                if (Country.countries.get(index) == c2){
                    return true;
                }
                return adjacencyCheck(Country.countries.get(index) , c2);
            }
        }
        return false;
    }

}
