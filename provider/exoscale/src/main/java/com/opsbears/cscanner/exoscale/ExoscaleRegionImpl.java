package com.opsbears.cscanner.exoscale;

import com.amazonaws.regions.RegionImpl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public class ExoscaleRegionImpl implements RegionImpl {
    private final String region;

    public ExoscaleRegionImpl(String region) {
        this.region = region;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDomain() {
        return null;
    }

    @Override
    public String getPartition() {
        return null;
    }

    @Override
    public boolean isServiceSupported(String s) {
        return false;
    }

    @Override
    public String getServiceEndpoint(String s) {
        return null;
    }

    @Override
    public boolean hasHttpEndpoint(String s) {
        return false;
    }

    @Override
    public boolean hasHttpsEndpoint(String s) {
        return false;
    }

    @Override
    public Collection<String> getAvailableEndpoints() {
        return null;
    }
}
