package com.prizmj.display;

/**
 * com.prizmj.display.Cardinal in PrizmJ
 */
public enum Cardinal {
    NORTH(1), SOUTH(2), EAST(3), WEST(4);

    final int side;

    Cardinal(int side) {
        this.side = side;
    }

    public int getSide() {
        return side;
    }

    public static Cardinal getOpposite(Cardinal cardinal) {
        if(cardinal == Cardinal.NORTH || cardinal == Cardinal.SOUTH)
            return (cardinal == Cardinal.NORTH) ? Cardinal.SOUTH : Cardinal.NORTH;
        else
            return (cardinal == Cardinal.EAST) ? Cardinal.WEST : Cardinal.EAST;
    }

}
