
/**
 * SuckyBeigeFish
 *  Cathal Killeen: 14300066
 *  Stan O'Neill: 14368166
 *  Adam Doran: 14325416
 */
public class Main {

    public static Boolean initialisationComplete = false;

    public static GameFrame GameFrame;

    public static void main(String[] args) {
        init();

        //TESTS

        //test nextPlayer function
        for(int i = 0; i<100; i++){
            //waits 2 seconds so the name changing can be seen
            try {
                Thread.sleep(2000);
            } catch(InterruptedException ex) {

            }

            System.out.println("Current player: " + Player.players.get(Player.nextPlayer()).name);
        }

        //console log all countries
        for (int i = 0; i < Country.countries.size(); i++) {
            System.out.println(Country.countries.get(i).toString());
        }

    }

    public static void init(){
        Country.createCountries();
        GameFrame = new GameFrame();
        printWelcomeMessage();
        Player.createPlayers();
        Player.setPlayerOrder();
        initialisationComplete = true;
    }

    public static void printWelcomeMessage(){
        GameFrame.SideBar.log("**************************************************\n"
                            + "                    WELCOME TO RISK!\n"
                            + "**************************************************\n",
                            GameFrame.SideBar.info);
    }

}