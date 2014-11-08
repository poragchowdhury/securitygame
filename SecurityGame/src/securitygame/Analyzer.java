package securitygame;

import java.util.Arrays;

/**
 * Calculates stats and performance of Defenders and Attackers
 * @author Oscar Veliz
 * @version 2014/11/07
 */
public class Analyzer
{
    int[][] points;
    String[] defenderNames;
    String[] attackerNames;
    int numAttackers;
    int numDefenders;
    int[] aTotals;
    int[] dTotals;
    double[] aAverage;
    double[] dAverage;
    double[] aMedian;
    double[] dMedian;
    double[] aStd;
    double[] dStd;
    double[] aRegret;
    double[] dRegret;
    double[] aBestOf;
    double[] dBestOf;


    public Analyzer(int[][] results, String[] aNames, String[] dNames)
    {
        points = results;
        defenderNames = dNames;
        attackerNames = aNames;
        numDefenders = dNames.length;
        numAttackers = aNames.length;

        aTotals = new int[numAttackers];
        dTotals = new int[numDefenders];
        //calculate totals
        for(int d = 0; d < points.length; d++)
            for(int a = 0; a < points[d].length; a++)
            {
                dTotals[d] += points[d][a];
                aTotals[a] += points[d][a];
            }

        //calculate averages
        //assumes arrays may not be the same length
        aAverage = new double[aTotals.length];
        dAverage = new double[dTotals.length];
        for(int a = 0; a < numAttackers; a++)
            aAverage[a] = (double)aTotals[a] / numDefenders;
        for(int d = 0; d < dAverage.length; d++)
            dAverage[d] = (double)dTotals[d] / numAttackers;

        //calculate medians
        aMedian = new double[numAttackers];
        dMedian = new double[numDefenders];
        for(int i = 0; i < dMedian.length; i++)
            dMedian[i] = median(points[i]);
        for(int j = 0; j < aMedian.length;j++)
        {
            int[] a  = new int[points.length];
            for(int i = 0; i < a.length; i++)
                a[i] = points[i][j];
            aMedian[j] = median(a);
        }

        //calculate standard deviations
        aStd = new double[numAttackers];
        dStd = new double[numDefenders];
        for(int d = 0; d < numDefenders; d++)
            dStd[d] = std(points[d],dAverage[d]);
        for(int a = 0; a < numAttackers; a++)
        {
            int[] x = new int[numDefenders];
            for(int d = 0; d < numDefenders; d++)
                x[d] = points[d][a];
            aStd[a] = std(x,aAverage[a]);
        }

        //calculate regrets and best of
        aRegret = new double[numAttackers];
        aBestOf = new double[numAttackers];
        dRegret = new double[numDefenders];
        dBestOf = new double[numDefenders];
        //for attacker
        for(int d = 0; d < points.length; d++)
        {
            int max = maximum(points[d]);
            for(int a = 0; a < points[d].length; a++)
            {
                aRegret[a] += (max - points[d][a]) / (double) numDefenders;
                if(points[d][a] == max)
                    aBestOf[a]++;
            }
        }
        //for defender
        for(int a = 0; a < numAttackers; a++)
        {
            int[] p = new int[numDefenders];
            for(int i = 0; i < numDefenders; i++)
                p[i] = points[i][a];
            int min = minimum(p);
            for(int d = 0; d < numDefenders; d++)
            {
                dRegret[d] += (p[d] - min) / (double) numAttackers;
                if(p[d] == min)
                    dBestOf[d]++;
            }
        }


        printResults();
        printAverages();
        printMedians();
        printStandardDev();
        printRegret();
        printBestOf();
    }

    public void print(double[] a, String[] s)
    {
        double[] ac = Arrays.copyOf(a,a.length);
        String[] sc = Arrays.copyOf(s,s.length);
        sort(ac,sc);
        System.out.println(Arrays.toString(sc));
        System.out.println(Arrays.toString(ac));
    }

    public void printResults()
    {
        System.out.print("\t");
        for (String attackerName : attackerNames)
            System.out.print(attackerName + "\t");
        System.out.println();
        for(int i = 0; i < points.length; i++)
        {
            System.out.print(defenderNames[i]+"\t");
            for(int j = 0; j < points[i].length; j++)
                System.out.print(points[i][j]+"\t\t");
            System.out.println();
        }
    }

    public void printAverages()
    {
        System.out.println("Average Points");
        print(aAverage,attackerNames);
        print(dAverage,defenderNames);
    }

    public void printMedians()
    {
        System.out.println("Medians");
        print(aMedian, attackerNames);
        print(dMedian,defenderNames);
    }

    public void printStandardDev()
    {
        System.out.println("Standard Deviations");
        print(aStd,attackerNames);
        print(dStd,defenderNames);
    }

    public void printRegret()
    {
        System.out.println("Average Regret");
        print(aRegret,attackerNames);
        print(dRegret,defenderNames);
    }

    public void printBestOf()
    {
        System.out.println("Instances Where Agent Was The Best");
        print(aBestOf,attackerNames);
        print(dBestOf,defenderNames);
    }

    public void sort(double[] a, String[] s)
    {
        for(int i = 0; i < a.length; i++)
        {
            for(int j = 0; j < a.length; j++)
            {
                if(a[j] < a[i])
                {
                    double tempA = a[j];
                    String tempS = s[j];
                    a[j] = a[i];
                    s[j] = s[i];
                    a[i] = tempA;
                    s[i] = tempS;
                }
            }
        }
    }

    public double median(int[] a)
    {
        int[] m = Arrays.copyOf(a,a.length);
        Arrays.sort(m);
        if(m.length%2==0)//even
            return (m[m.length/2-1]+m[m.length/2])/2.0;
        else//odd
            return m[m.length/2];
    }

    public double std(int[] a, double avg)
    {
        int[] m = Arrays.copyOf(a,a.length);
        double var = 0;
        for (int aM : m)
            var += Math.pow((double) aM - avg, 2);
        return Math.sqrt(var/m.length);
    }

    public int maximum(int[] a)
    {
        int max = a[0];
        for(int i = 1; i < a.length; i++)
            if(max < a[i])
                max = a[i];
        return max;
    }

    public int minimum(int[] a)
    {
        int min = a[0];
        for(int i = 1; i < a.length; i++)
            if(min > a[i])
                min = a[i];
        return  min;
    }

    public static void main(String[] args){
        String[] aS = {"Larry","Curly","Moe"};
        String[] dS = {"Bonnie","Clyde",};
        int[][] results = new int[2][3];
        results[0][0] = 9;results[0][1]=7;results[0][2]=6;
        results[1][0] = 2;results[1][1]=3;results[1][2]=6;
        new Analyzer(results,aS,dS);
    }
}
