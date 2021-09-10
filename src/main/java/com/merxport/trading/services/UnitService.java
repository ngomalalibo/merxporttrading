package com.merxport.trading.services;

import com.merxport.trading.entities.Unit;

public interface UnitService
{
    Unit save(Unit unit);
    
    Unit delete(Unit unit);
}
