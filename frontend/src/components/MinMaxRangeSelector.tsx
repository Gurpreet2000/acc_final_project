import { useEffect, useState } from 'react';
import { Label } from './ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from './ui/select';

const MinMaxRangeSelector = (props: {
  value: { min: any; max: any };
  setValue: (e: { min: any; max: any }) => void;
  minList: { label: any; value: any }[];
  maxList: { label: any; value: any }[];
  label: string;
}) => {
  const [forceReload, setForceReload] = useState(false);
  const [range, setRange] = useState<{ min: any; max: any }>({
    min: null,
    max: null,
  });

  useEffect(() => {
    props.setValue(range);
  }, [range]);

  return (
    <div className="flex flex-col">
      <div className="font-bold">
        {props?.label}
        <a
          className=" font-medium underline text-sm ml-2 cursor-pointer"
          onClick={() => {
            setRange({ max: null, min: null });
            setForceReload(p => !p);
          }}
        >
          Clear
        </a>
      </div>

      <div className="flex flex-row gap-5">
        <div>
          <Label htmlFor="MinMaxRangeSelector">Min</Label>
          <Select
            key={forceReload + ''}
            onValueChange={e => setRange(prev => ({ ...prev, min: e }))}
            value={range?.min || undefined}
          >
            <SelectTrigger className="w-[50%] min-w-[125px]">
              <SelectValue placeholder="Select" />
            </SelectTrigger>
            <SelectContent id="MinMaxRangeSelector">
              {props?.minList.map((e: { value: any; label: string }) => (
                <SelectItem key={e?.value + ' ' + e?.label} value={e?.value}>
                  {e?.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div>
          <Label htmlFor="MinMaxRangeSelector">Max</Label>
          <Select
            key={forceReload + ''}
            onValueChange={e => setRange(prev => ({ ...prev, max: e }))}
            value={range?.max || undefined}
          >
            <SelectTrigger className="w-[50%] min-w-[125px]">
              <SelectValue placeholder="Select" />
            </SelectTrigger>
            <SelectContent id="MinMaxRangeSelector">
              {props.maxList.map((e: { value: any; label: string }) => (
                <SelectItem key={e?.value + ' ' + e?.label} value={e?.value}>
                  {e?.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>
  );
};

export default MinMaxRangeSelector;
