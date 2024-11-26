import { useEffect, useState } from 'react';
import apiCall from '@/lib/apiCall';
import { Label } from './ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from './ui/select';
import { parseCapacity } from '@/lib/utils';

const StorageSelector = (props: {
  value: { min: any; max: any };
  setValue: (e: { min: any; max: any }) => void;
}) => {
  const [capacities, setCapacities] = useState<
    {
      label: string;
      value: any;
    }[]
  >([]);
  const [range, setRange] = useState<{ min: any; max: any }>({
    min: 0,
    max: 0,
  });

  useEffect(() => {
    apiCall
      .get('/storage_list')
      .then(res => {
        const sortedList = res?.data?.data
          .map((e: any) => +e)
          .sort((a: number, b: number) => a > b);
        setCapacities(
          sortedList.map((e: number) => ({
            label: parseCapacity(+e),
            value: +e,
          }))
        );
      })
      .catch(err => {
        console.error(err);
      });
  }, []);

  useEffect(() => {
    setRange({
      min: capacities[0]?.value,
      max: capacities[capacities?.length - 1]?.value,
    });
  }, [capacities]);

  useEffect(() => {
    console.log({
      capacities,

      minList: capacities
        .filter(e => (range.max ? +e?.value < +range.max : true))
        .map(e => e?.value),
      maxList: capacities
        .filter(e => (range.min ? +e?.value > +range.min : true))
        .map(e => e?.value),
    });
    props.setValue(range);
  }, [range]);

  return (
    <div className="flex flex-col">
      <div className="font-bold">Select Capacity</div>

      <div className="flex flex-row gap-5">
        <div>
          <Label htmlFor="storageSelector">Min</Label>
          <Select onValueChange={e => setRange(prev => ({ ...prev, min: e }))}>
            <SelectTrigger className="w-[50%] min-w-[125px]">
              <SelectValue placeholder="Select" />
            </SelectTrigger>
            <SelectContent id="storageSelector">
              {capacities
                .filter(e => (range.max ? +e?.value <= +range.max : true))
                .map((e: { value: any; label: string }) => (
                  <SelectItem value={e?.value}>{e?.label}</SelectItem>
                ))}
            </SelectContent>
          </Select>
        </div>
        <div>
          <Label htmlFor="storageSelector">Max</Label>
          <Select onValueChange={e => setRange(prev => ({ ...prev, max: e }))}>
            <SelectTrigger className="w-[50%] min-w-[125px]">
              <SelectValue placeholder="Select" />
            </SelectTrigger>
            <SelectContent id="storageSelector">
              {capacities
                .filter(e => (range.min ? +e?.value >= +range.min : true))
                .map((e: { value: any; label: string }) => (
                  <SelectItem value={e?.value}>{e?.label}</SelectItem>
                ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>
  );
};

export default StorageSelector;
