package de.fatzzke.util;

import net.neoforged.neoforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 5000);
    }

    public void changeEnergy(int amount) {

        this.energy = this.energy + amount;

        if (this.energy < 0)
            this.energy = 0;

        if (this.energy > this.capacity)
            this.energy = this.capacity;

    }

    public void setEnerrgy(int amount){
        this.energy = amount;
    }

}
