package defaultpack;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	
	private Clip audioClip;
	private long clipLength; 
	private long currFrame;
	
	public Sound(String location){
	      try {
	          // Open an audio input stream.
	          URL url = this.getClass().getClassLoader().getResource(location);
	          AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
	          
	          // Get a sound clip resource.
	          audioClip = AudioSystem.getClip();
	          
	          // Open audio clip and load samples from the audio input stream.
	          audioClip.open(audioIn);
	          //flip.loop(Clip.LOOP_CONTINUOUSLY);
	          
	         // clip.start();
	       } catch (UnsupportedAudioFileException e) {
	          e.printStackTrace();
	       } catch (IOException e) {
	          e.printStackTrace();
	       } catch (LineUnavailableException e) {
	          e.printStackTrace();
	       }
	      
	      clipLength = audioClip.getMicrosecondLength();
	}
	
	public Clip getClip(){
		return audioClip;
	}
	
	public long audioLength(){
		return clipLength;
	}
	
	
	
	public void play(){
		audioClip.setMicrosecondPosition(0);
		audioClip.start();
	}
	
	public void loopAudio(int repeats){
		audioClip.setMicrosecondPosition(0);
		audioClip.loop(repeats);
	}
	
	
	public void loopForever(){
		audioClip.setMicrosecondPosition(0);
		audioClip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop(){
		audioClip.stop();
	}	

	public void pause(){
		currFrame = audioClip.getLongFramePosition();
		audioClip.stop();
	}
	
	public void resume(){
		audioClip.setMicrosecondPosition(currFrame);
		audioClip.start();
	}
	
	
	public void closeStream(){
		audioClip.close();
	}
}
