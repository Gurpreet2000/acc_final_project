import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

const Home = () => {
  return (
    <div className=" flex flex-col gap-5">
      <span className="bg-sky-900 bg-opacity-55 h-[25vh] text-4xl font-bold text-center flex flex-col justify-center">
        Cloud storage analyzer
      </span>
      <div
        className="bg-sky-900 bg-opacity-25  h-[25vh]  flex flex-col justify-center gap-2"
        style={{ alignItems: 'center' }}
      >
        <span className="text-xl font-bold text-center mb-2">
          Subscribe to our newsletter
        </span>
        <Input type="email" placeholder="abc@email.com" className="w-[50%]" />
        <Button>Subscribe</Button>
      </div>
    </div>
  );
};

export default Home;
